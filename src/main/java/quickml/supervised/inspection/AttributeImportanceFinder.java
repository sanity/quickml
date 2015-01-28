package quickml.supervised.inspection;

import com.google.common.collect.*;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.PredictiveModel;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.CrossValidatorBuilder;
import quickml.supervised.crossValidation.crossValLossFunctions.*;
import quickml.data.*;
import quickml.supervised.PredictiveModelBuilder;
import com.google.common.base.Optional;

import java.io.Serializable;
import java.util.*;

//TODO[mk] Can this be removed/updated??
public class AttributeImportanceFinder {
    private static final Logger logger = LoggerFactory.getLogger(AttributeImportanceFinder.class);
    private Set<String> attributesToNotRemove = Sets.newHashSet();
    private Map<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> overallBestAttributesWithLosses = Maps.newHashMap();
    private Map<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> bestNAttributesWithLosses = Maps.newHashMap();
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

//    public <PM extends PredictiveModel<AttributesMap, PredictionMap>, PMB extends PredictiveModelBuilder<AttributesMap, Serializable, PM>>
//    AttributeImportanceFinderSummary determineAttributeImportance
//            (CrossValidatorBuilder<AttributesMap, Serializable, PredictionMap> crossValidatorBuilder, PredictiveModelBuilderFactory<AttributesMap, Serializable, PM, PMB> predictiveModelBuilderFactory,
//             Map<String, Object> config, Iterable<? extends Instance<AttributesMap, Serializable>> trainingData, int iterations, double percentageOfFeaturesToRemovePerIteration,
//             String primaryLossFunction, Map<String, CrossValLossFunction<Serializable, PredictionMap>> crossValLossFunctionMap) {
//
//        Set<String> attributes = getAllAttributesInTrainingSet(trainingData);
//        String noAttributesRemoved = "noAttributesRemoved";
//        attributes.add(noAttributesRemoved);
//
//        //do recursive feature elimination
//        double bestPrimaryLossSeenSoFar = Double.MAX_VALUE;
//        List<Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>>> attributesWithLosses = Lists.newArrayList();
//        boolean startedTrackingBestAttributes = false;
//        for (int i = 0; i < iterations; i++) {
//            CrossValidator<AttributesMap, Serializable, PredictionMap> crossValidator = crossValidatorBuilder.createCrossValidator();
//         /*   crossValLossFunctionMap = Maps.newHashMap();
//            crossValLossFunctionMap.put("log", new ClassifierLogCVLossFunction(.000001));
//            crossValLossFunctionMap.put("AUC", new WeightedAUCCrossValLossFunction(1.0));
//            crossValLossFunctionMap.put("logLossCorrectedForDownSampling", new LossFunctionCorrectedForDownsampling(new ClassifierLogCVLossFunction(0.000001), 0.99, Double.valueOf(0.0)));
//*/
////            attributesWithLosses = crossValidator.getAttributeImportances(predictiveModelBuilderFactory, config, trainingData, primaryLossFunction, attributes, crossValLossFunctionMap);
//            double currentPrimaryLoss = getModelLoss(attributesWithLosses).get(primaryLossFunction);
//            if (attributesWithLosses.size() <= maxAttributesInOptimalSet && !startedTrackingBestAttributes) {
//                startedTrackingBestAttributes = true;
//                bestPrimaryLossSeenSoFar = currentPrimaryLoss;
//            }
//            bestPrimaryLossSeenSoFar = updateBestAttributesWithLosseIfNeccessary(primaryLossFunction, currentPrimaryLoss, bestPrimaryLossSeenSoFar, attributesWithLosses);
//            updateBestNAttributesWithLosseIfNeccessary(attributesWithLosses);
//            logger.info("model losses: " + getModelLoss(attributesWithLosses).toString() + ", at iteration: " + i + "out of iterations: " + iterations);
//
//      /*      for (Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> pair : attributesWithLosses) {
//                logger.info("attribute: " + pair.getValue0() + ".  losses: " + pair.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss());
//            }
//      */      trainingData = updateAttributesUsedInTrainingAndBestAttributes(trainingData, attributesWithLosses, attributes, percentageOfFeaturesToRemovePerIteration);
//        }
//        overallBestAttributesWithLosses.remove(noAttributesRemoved);
//
//        AttributeImportanceFinderSummary attributeImportanceFinderSummary = getAttributeImportanceFinderSummary();
//        return attributeImportanceFinderSummary;
//    }

    private AttributeImportanceFinderSummary getAttributeImportanceFinderSummary() {
        AttributeImportanceFinderSummary attributeImportanceFinderSummary;
        if (desiredNumberOfAttributesInOptimalSet.isPresent()) {
            attributeImportanceFinderSummary = new AttributeImportanceFinderSummary(overallBestAttributesWithLosses, bestNAttributesWithLosses);
        } else {
            attributeImportanceFinderSummary = new AttributeImportanceFinderSummary(overallBestAttributesWithLosses);
        }
        return attributeImportanceFinderSummary;
    }

    private double updateBestAttributesWithLosseIfNeccessary(String primaryLossFunction, double currentPrimaryLoss, double bestPrimaryLossSeenSoFar,
                                                             List<Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>>> attributesWithLosses) {
        if (currentPrimaryLoss <= bestPrimaryLossSeenSoFar && attributesWithLosses.size() < maxAttributesInOptimalSet) {
            bestPrimaryLossSeenSoFar = currentPrimaryLoss;
            updateBestAttributesWithLosses(primaryLossFunction, attributesWithLosses);
        }
        return bestPrimaryLossSeenSoFar;
    }

    private void updateBestNAttributesWithLosseIfNeccessary(List<Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>>> attributesWithLosses) {
        if (desiredNumberOfAttributesInOptimalSet.isPresent() && attributesWithLosses.size() < desiredNumberOfAttributesInOptimalSet.get() && !gotBestNAttributesWithLosses) {
            gotBestNAttributesWithLosses = true;
            bestNAttributesWithLosses = Maps.newHashMap();
            for (Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> pair : attributesWithLosses) {
                bestNAttributesWithLosses.put(pair.getValue0(), pair.getValue1());
            }
        }
    }

    private void updateBestAttributesWithLosses(String primaryLossFunction, List<Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>>> attributesWithLosses) {
        overallBestAttributesWithLosses = Maps.newHashMap();
        for (Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> pair : attributesWithLosses) {
            overallBestAttributesWithLosses.put(pair.getValue0(), pair.getValue1());
        }
        logger.info("best attributes so far are: ");
        for (Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> pair : attributesWithLosses) {
            logger.info("attribute: " + pair.getValue0() + ".  losses: " + pair.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss());
        }
    }

    private Map<String, Double> getModelLoss(List<Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>>> attributesWithLosses) {
        for (int i = attributesWithLosses.size() - 1; i >= 0; i--) {
            if (attributesWithLosses.get(i).getValue0().equals("noAttributesRemoved")) {
                return attributesWithLosses.get(i).getValue1().getLossMap();
            }
        }
        return null;
    }

    private List<Instance<AttributesMap, Serializable>> updateAttributesUsedInTrainingAndBestAttributes(final Iterable<? extends Instance<AttributesMap, Serializable>> trainingData, List<Pair<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>>> attributesWithLosses,
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
        List<Instance<AttributesMap, Serializable>> newInstances = Lists.newArrayList();
        for (Instance<AttributesMap, Serializable> instance : trainingData) {
            AttributesMap attributes = instance.getAttributes();
            AttributesMap newAttributes = AttributesMap.newHashMap();
            for (String attributeName : attributes.keySet()) {
                if (!attributesToRemove.contains(attributeName))
                    newAttributes.put(attributeName, attributes.get(attributeName));
            }
            newInstances.add(new InstanceImpl<>(newAttributes, instance.getLabel(), instance.getWeight()));
        }
        return newInstances;
    }

    private Set<String> getAllAttributesInTrainingSet(Iterable<? extends Instance<AttributesMap, Serializable>> trainingData) {
        Set<String> attributes = Sets.newHashSet();
        for (Instance<AttributesMap, Serializable> instance : trainingData) {
            attributes.addAll(instance.getAttributes().keySet());
        }
        return attributes;
    }

    public static class AttributeImportanceFinderSummary {
        public Map<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> overallBestAttributesWithLosses;
        public Optional<Map<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>>> bestNAttributesWithLosses;

        private AttributeImportanceFinderSummary(Map<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> overallBestAttributesWithLosses) {
            this.overallBestAttributesWithLosses = overallBestAttributesWithLosses;
        }

        private AttributeImportanceFinderSummary(Map<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> overallBestAttributesWithLosses,
                                                 Map<String, MultiLossFunctionWithModelConfigurations<Serializable, PredictionMap>> bestNAttributesWithLosses) {
            this.overallBestAttributesWithLosses = overallBestAttributesWithLosses;
            this.bestNAttributesWithLosses = Optional.of(bestNAttributesWithLosses);
        }
    }
}