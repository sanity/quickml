package quickml.supervised.inspection;

import com.google.common.collect.*;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.CrossValidatorBuilder;
import quickml.supervised.crossValidation.StationaryCrossValidatorBuilder;
import quickml.supervised.crossValidation.crossValLossFunctions.*;
import quickml.data.*;
import quickml.supervised.PredictiveModelBuilder;

import java.util.*;

public class AttributeImportanceFinder {
    private static final Logger logger = LoggerFactory.getLogger(AttributeImportanceFinder.class);
    Set<String> attributesToNotRemove = Sets.newHashSet();

    public AttributeImportanceFinder() {

    }
    public AttributeImportanceFinder(Set<String> attributesToNotRemove) {
        this.attributesToNotRemove = attributesToNotRemove;
    }

    public<PM extends PredictiveModel<AttributesMap, PredictionMap>,  PMB extends PredictiveModelBuilder<AttributesMap, PM>>
            List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> determineAttributeImportance
         (CrossValidatorBuilder<AttributesMap, PredictionMap> crossValidatorBuilder, PredictiveModelBuilderFactory<AttributesMap,  PM, PMB> predictiveModelBuilderFactory,
          Map<String, Object> config, Iterable<? extends Instance<AttributesMap>> trainingData, int iterations, double percentageOfFeaturesToRemovePerIteration,
          String primaryLossFunction, Map<String, CrossValLossFunction<PredictionMap>> crossValLossFunctionMap) {

        Set<String> attributes = getAllAttributesInTrainingSet(trainingData);
        String noAttributesRemoved = "noAttributesRemoved";
        attributes.add(noAttributesRemoved);

        //do recursive feature elimination
        List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses = Lists.newArrayList();
        for (int i = 0; i < iterations; i++) {
            CrossValidator<AttributesMap, PredictionMap> crossValidator = crossValidatorBuilder.createCrossValidator();
            crossValLossFunctionMap = Maps.newHashMap();
            crossValLossFunctionMap.put("log", new ClassifierLogCVLossFunction(.000001));
            crossValLossFunctionMap.put("AUC", new WeightedAUCCrossValLossFunction(1.0));
            crossValLossFunctionMap.put("logLossCorrectedForDownSampling", new LossFunctionCorrectedForDownsampling(new ClassifierLogCVLossFunction(0.000001), 0.99, Double.valueOf(0.0)));

            attributesWithLosses = crossValidator.getAttributeImportances(predictiveModelBuilderFactory, config, trainingData, primaryLossFunction, attributes, crossValLossFunctionMap);
            if (i < iterations - 1) {
                trainingData = updateAttributesUsedInTraining(trainingData, attributesWithLosses, attributes, percentageOfFeaturesToRemovePerIteration);
            }
            logger.info("model losses" + getModelLoss(attributesWithLosses).toString());
            for (Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> pair : attributesWithLosses) {
                logger.info("attribute: " + pair.getValue0() + ".  losses: " + pair.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss());
            }
        }

        for (int i = 0; i< attributesWithLosses.size(); i++) {
            Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>> pair = attributesWithLosses.get(i);
            if(pair.getValue0().equals(noAttributesRemoved)) {
                attributesWithLosses.remove(i);
                break;
            }
        }
        return attributesWithLosses;
    }

    private Map<String, Double> getModelLoss( List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses) {
        for (int i = attributesWithLosses.size() - 1; i >= 0; i--) {
            if (attributesWithLosses.get(i).getValue0().equals("noAttributesRemoved")) {
                return attributesWithLosses.get(i).getValue1().getLossMap();
            }
        }
        return null;
    }

    private List<Instance<AttributesMap>> updateAttributesUsedInTraining(final Iterable<? extends Instance<AttributesMap>> trainingData, List<Pair<String, MultiLossFunctionWithModelConfigurations<PredictionMap>>> attributesWithLosses,
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
    /*    attributesToRemove.add("eap");
        attributesToRemove.add("ecp");
        allAttributes.remove("eap");
        allAttributes.remove("ecp");
*/
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
}