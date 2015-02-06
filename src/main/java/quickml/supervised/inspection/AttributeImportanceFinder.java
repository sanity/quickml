package quickml.supervised.inspection;

import com.google.common.collect.*;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.CrossValidatorBuilder;
import quickml.supervised.crossValidation.crossValLossFunctions.*;
import quickml.data.*;
import quickml.supervised.PredictiveModelBuilder;
import com.google.common.base.Optional;

import java.util.*;

public class AttributeImportanceFinder {
    private static final Logger logger = LoggerFactory.getLogger(AttributeImportanceFinder.class);
    private Set<String> attributesToNotRemove = Sets.newHashSet();
    private Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> overallBestAttributesWithLosses = Maps.newHashMap();
    private Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> bestMaximalSetOfAttributesWithLosses = Maps.newHashMap();
    private Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> bestMinimalSetOfAttributesWithLosses = Maps.newHashMap();
    private boolean setBestMaximalSetOfAttributesWithLosses = false;
    private boolean gotBestNAttributesWithLosses = false;
    private Optional<Integer> desiredNumberOfAttributesInOptimalSet = Optional.absent();
    private int maxAttributesInOptimalSet = Integer.MAX_VALUE; //setting a smaller value allows one to enforce a minimum degree of sparseness

    //List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> overallBestAttributesWithLosses;
    public AttributeImportanceFinder() {

    }

    public AttributeImportanceFinder(Set<String> attributesToNotRemove) {
        this.attributesToNotRemove = attributesToNotRemove;
    }

    public AttributeImportanceFinder(Set<String> attributesToNotRemove, int desiredNumberOfAttributesInOptimalSet) {
        this.attributesToNotRemove = attributesToNotRemove;
        this.desiredNumberOfAttributesInOptimalSet = Optional.of(desiredNumberOfAttributesInOptimalSet);
    }

    public AttributeImportanceFinder(Set<String> attributesToNotRemove, int desiredNumberOfAttributesInOptimalSet, int minAttributesInOptimalSet) {
        this.attributesToNotRemove = attributesToNotRemove;
        this.desiredNumberOfAttributesInOptimalSet = Optional.of(desiredNumberOfAttributesInOptimalSet);
        this.maxAttributesInOptimalSet = minAttributesInOptimalSet;
    }

    public <PM extends PredictiveModel<AttributesMap, PredictionMap>, PMB extends PredictiveModelBuilder<AttributesMap, PM>>
    AttributeImportanceFinderSummary determineAttributeImportance
            (CrossValidatorBuilder<AttributesMap, PredictionMap> crossValidatorBuilder, PredictiveModelBuilderFactory<AttributesMap, PM, PMB> predictiveModelBuilderFactory,
             Map<String, Object> config, Iterable<? extends Instance<AttributesMap>> trainingData, int iterations, double percentageOfFeaturesToRemovePerIteration,
             String primaryLossFunction, Map<String, CrossValLossFunction<PredictionMap>> crossValLossFunctionMap) {

        Set<String> attributes = getAllAttributesInTrainingSet(trainingData);
        String noAttributesRemoved = "noAttributesRemoved";
        attributes.add(noAttributesRemoved);

        //do recursive feature elimination
        double bestPrimaryLossSeenSoFar = Double.MAX_VALUE;
        List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses = Lists.newArrayList();
        boolean startedTrackingBestAttributes = false;
        for (int i = 0; i < iterations; i++) {
            CrossValidator<AttributesMap, PredictionMap> crossValidator = crossValidatorBuilder.createCrossValidator();
         /*   crossValLossFunctionMap = Maps.newHashMap();
            crossValLossFunctionMap.put("log", new ClassifierLogCVLossFunction(.000001));
            crossValLossFunctionMap.put("AUC", new WeightedAUCCrossValLossFunction(1.0));
            crossValLossFunctionMap.put("logLossCorrectedForDownSampling", new LossFunctionCorrectedForDownsampling(new ClassifierLogCVLossFunction(0.000001), 0.99, Double.valueOf(0.0)));
*/
            attributesWithLosses = crossValidator.getAttributeImportances(predictiveModelBuilderFactory, config, trainingData, primaryLossFunction, attributes, crossValLossFunctionMap);
            double currentPrimaryLoss = getModelLoss(attributesWithLosses).get(primaryLossFunction);
            if (attributesWithLosses.size() <= maxAttributesInOptimalSet && !startedTrackingBestAttributes) {
                startedTrackingBestAttributes = true;
                bestPrimaryLossSeenSoFar = currentPrimaryLoss;
            }
            bestPrimaryLossSeenSoFar = updateBestAttributesWithLossesIfNeccessary(primaryLossFunction, currentPrimaryLoss, bestPrimaryLossSeenSoFar, attributesWithLosses);
            updateBestMinimalSetOfAttributesWithLossesIfNeccessary(attributesWithLosses);
            logger.info("model losses: " + getModelLoss(attributesWithLosses).toString() + ", at iteration: " + i + "out of iterations: " + iterations);

      /*      for (Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> pair : attributesWithLosses) {
                logger.info("attribute: " + pair.getValue0() + ".  losses: " + pair.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss());
            }
      */      trainingData = updateAttributesUsedInTrainingAndBestAttributes(trainingData, attributesWithLosses, attributes, percentageOfFeaturesToRemovePerIteration);
        }
        overallBestAttributesWithLosses.remove(noAttributesRemoved);

        AttributeImportanceFinderSummary attributeImportanceFinderSummary = getAttributeImportanceFinderSummary();
        return attributeImportanceFinderSummary;
    }

    private AttributeImportanceFinderSummary getAttributeImportanceFinderSummary() {
        AttributeImportanceFinderSummary attributeImportanceFinderSummary;
        if (desiredNumberOfAttributesInOptimalSet.isPresent()) {
            attributeImportanceFinderSummary = new AttributeImportanceFinderSummary(overallBestAttributesWithLosses, bestMinimalSetOfAttributesWithLosses, bestMaximalSetOfAttributesWithLosses);
        } else {
            attributeImportanceFinderSummary = new AttributeImportanceFinderSummary(overallBestAttributesWithLosses, bestMaximalSetOfAttributesWithLosses);
        }
        return attributeImportanceFinderSummary;
    }

    private double updateBestAttributesWithLossesIfNeccessary(String primaryLossFunction, double currentPrimaryLoss, double bestPrimaryLossSeenSoFar,
                                                              List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses) {

        int numActualAttributes = attributesWithLosses.size()-1;
        logger.info("numActualAttributse: " + numActualAttributes + "\n" + "maxAttributesInOptimalSet: " + maxAttributesInOptimalSet);
        logger.info("currentPrimaryLoss " + currentPrimaryLoss + "\n" +"bestPrimaryLoss" + bestPrimaryLossSeenSoFar);
        if (currentPrimaryLoss <= bestPrimaryLossSeenSoFar && numActualAttributes <= maxAttributesInOptimalSet) {
            bestPrimaryLossSeenSoFar = currentPrimaryLoss;
            updateBestAttributesWithLosses(primaryLossFunction, attributesWithLosses);
        }
        return bestPrimaryLossSeenSoFar;
    }

    private void updateBestMinimalSetOfAttributesWithLossesIfNeccessary(List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses) {
        if (desiredNumberOfAttributesInOptimalSet.isPresent() && attributesWithLosses.size() <= desiredNumberOfAttributesInOptimalSet.get() && !gotBestNAttributesWithLosses) {
            gotBestNAttributesWithLosses = true;
            bestMinimalSetOfAttributesWithLosses = Maps.newHashMap();
            for (Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> pair : attributesWithLosses) {
                bestMinimalSetOfAttributesWithLosses.put(pair.getValue0(), pair.getValue1());
            }
        }
    }

    private void updateBestAttributesWithLosses(String primaryLossFunction, List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses) {

        if (!this.setBestMaximalSetOfAttributesWithLosses) {
            updateMapOfAttributeValsToLoss(bestMaximalSetOfAttributesWithLosses, attributesWithLosses);
        }
        updateMapOfAttributeValsToLoss(overallBestAttributesWithLosses, attributesWithLosses);
        logger.info("best attributes so far are: ");
        for (Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> pair : attributesWithLosses) {
            logger.info("attribute: " + pair.getValue0() + ".  losses: " + pair.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss());
        }
    }

    private void updateMapOfAttributeValsToLoss(Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> attributesToLossesMap, List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses) {
        attributesToLossesMap.clear();
        for (Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> pair : attributesWithLosses) {
            attributesToLossesMap.put(pair.getValue0(), pair.getValue1());
        }
    }

    private Map<String, Double> getModelLoss(List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses) {
        for (int i = attributesWithLosses.size() - 1; i >= 0; i--) {
            if (attributesWithLosses.get(i).getValue0().equals("noAttributesRemoved")) {
                return attributesWithLosses.get(i).getValue1().getLossMap();
            }
        }
        return null;
    }

    private List<Instance<AttributesMap>> updateAttributesUsedInTrainingAndBestAttributes(final Iterable<? extends Instance<AttributesMap>> trainingData, List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses,
                                                                                          Set<String> allAttributes, double percentageOfAttributesToRemoveAtEachIteration) {
        int numberOfAttributesToRemove = (int) (percentageOfAttributesToRemoveAtEachIteration * allAttributes.size());
        Set<String> attributesToRemove = Sets.newHashSet();
        for (int i = attributesWithLosses.size() - 1; i >= Math.max(0, attributesWithLosses.size() - 1 - numberOfAttributesToRemove); i--) {
            String attributeToRemove = attributesWithLosses.get(i).getValue0();
            if (!attributesToNotRemove.contains(attributeToRemove)) {
                attributesToRemove.add(attributeToRemove);
                allAttributes.remove(attributeToRemove);
            }
        }
        allAttributes.add("noAttributesRemoved");

        //remove attributes from training data
        List<Instance<AttributesMap>> newInstances = Lists.newArrayList();
        for (Instance<AttributesMap> instance : trainingData) {
            AttributesMap attributes = instance.getAttributes();
            AttributesMap newAttributes = AttributesMap.newHashMap();
            for (String attributeName : attributes.keySet()) {
                if (!attributesToRemove.contains(attributeName))
                    newAttributes.put(attributeName, attributes.get(attributeName));
            }
            newInstances.add(new InstanceImpl<AttributesMap>(newAttributes, instance.getLabel(), instance.getWeight()));
        }
        return newInstances;
    }

    private Set<String> getAllAttributesInTrainingSet(Iterable<? extends Instance<AttributesMap>> trainingData) {
        Set<String> attributes = Sets.newHashSet();
        for (Instance<AttributesMap> instance : trainingData) {
            attributes.addAll(instance.getAttributes().keySet());
        }
        return attributes;
    }

    public static class AttributeImportanceFinderSummary {
        public Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> overallBestAttributesWithLosses;
        public Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> bestMaximalSetOfAttributesWithLosses;
        public Optional<Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> bestMinimalSetOfAttributesWithLosses = Optional.absent();

        private AttributeImportanceFinderSummary(Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> overallBestAttributesWithLosses,
                                                 Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> bestMaximalSetOfAttributesWithLosses) {
            this.overallBestAttributesWithLosses = overallBestAttributesWithLosses;
            this.bestMaximalSetOfAttributesWithLosses = bestMaximalSetOfAttributesWithLosses;
        }

        private AttributeImportanceFinderSummary(Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> overallBestAttributesWithLosses,
                                                 Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> bestMinimalSetOfAttributesWithLosses,
                                                 Map<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> bestMaximalSetOfAttributesWithLosses
                                                 ) {
            this(overallBestAttributesWithLosses, bestMaximalSetOfAttributesWithLosses);
            this.bestMinimalSetOfAttributesWithLosses = Optional.of(bestMinimalSetOfAttributesWithLosses);
        }
    }
}