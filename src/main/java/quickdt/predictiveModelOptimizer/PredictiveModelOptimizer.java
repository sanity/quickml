package quickdt.predictiveModelOptimizer;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.Misc;
import quickdt.crossValidation.CrossValidator;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.*;

import java.util.Collections;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class PredictiveModelOptimizer<PM extends PredictiveModel, PMB extends PredictiveModelBuilder<PM>> {
    private static final Logger logger = LoggerFactory.getLogger(PredictiveModelOptimizer.class);
    private final PredictiveModelBuilderBuilder<PM, PMB> predictiveModelBuilderBuilder;
    private final CrossValidator crossValidator;
    private final Map<String, FieldValueRecommender> valueRecommenders;

    public PredictiveModelOptimizer(PredictiveModelBuilderBuilder<PM, PMB> predictiveModelBuilderBuilder) {
        this(predictiveModelBuilderBuilder, new CrossValidator(4));
    }

    public PredictiveModelOptimizer(PredictiveModelBuilderBuilder<PM, PMB> predictiveModelBuilderBuilder, CrossValidator crossValidator) {
        this(predictiveModelBuilderBuilder, crossValidator, predictiveModelBuilderBuilder.createDefaultParametersToOptimize());
    }

    public PredictiveModelOptimizer(PredictiveModelBuilderBuilder<PM, PMB> predictiveModelBuilderBuilder, CrossValidator crossValidator, Map<String, FieldValueRecommender> valueRecommenders) {
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.crossValidator = crossValidator;
        this.valueRecommenders = valueRecommenders;
    }

    public Map<String, Object> determineOptimalConfiguration(final Iterable<? extends AbstractInstance> trainingData) {
        Map<String, Object> startingConfiguration = Maps.newHashMap();
        for (Map.Entry<String, FieldValueRecommender> stringFieldValueRecommenderEntry : valueRecommenders.entrySet()) {
            final Optional<Object> firstValueOptional = stringFieldValueRecommenderEntry.getValue().recommendNextValue(Collections.<Object, Double>emptyMap());
            if (!firstValueOptional.isPresent()) {
                throw new RuntimeException("Failed to retrieve initial value for field " + stringFieldValueRecommenderEntry.getKey());
            }
            startingConfiguration.put(stringFieldValueRecommenderEntry.getKey(), firstValueOptional.get());
        }
        return determineOptimalConfiguration(trainingData, startingConfiguration);
    }


    public Map<String, Object> determineOptimalConfiguration(final Iterable<? extends AbstractInstance> trainingData, Map<String, Object> startingConfiguration) {
        Map<String, Object> bestConfigurationSoFar = Maps.newHashMap(startingConfiguration);
        final Map<Map<String, Object>, Double> configurationLosses = Maps.newHashMap();
        while (true) {
            final ObjectWithLoss<Map<String, Object>> newBestConfigurationWithLoss = iterateAndImproveConfiguration(trainingData, configurationLosses, bestConfigurationSoFar);
            if (newBestConfigurationWithLoss.get().equals(bestConfigurationSoFar)) {
                logger.info("Best configuration unchanged after iteration, we're done here.  Configuration: "+newBestConfigurationWithLoss.get()+" with loss: "+newBestConfigurationWithLoss.getLoss());
                break;
            } else {
                bestConfigurationSoFar = newBestConfigurationWithLoss.get();
                logger.info("Found new best configuration after iteration: "+bestConfigurationSoFar+" with loss "+newBestConfigurationWithLoss.getLoss());
            }
        }
        return bestConfigurationSoFar;
    }

    private ObjectWithLoss<Map<String, Object>> iterateAndImproveConfiguration(final Iterable<? extends AbstractInstance> trainingData,
                                                                               final Map<Map<String, Object>, Double> configurationLoss,
                                                                               Map<String, Object> startingConfiguration) {
        Map<String, Object> currentConfiguration = Maps.newHashMap(startingConfiguration);
        double currentConfigurationLoss = Double.MAX_VALUE;
        for (Map.Entry<String, FieldValueRecommender> stringFieldValueRecommenderEntry : valueRecommenders.entrySet()) {
            String fieldName = stringFieldValueRecommenderEntry.getKey();
            logger.info("Optimizing field '" + fieldName + "'");
            final Map<Object, Double> scoresForFieldValues = getScoresForFieldValues(trainingData, currentConfiguration, configurationLoss, fieldName, stringFieldValueRecommenderEntry.getValue());
            final Optional<Map.Entry<Object, Double>> entryWithLowestValueOpt = Misc.getEntryWithLowestValue(scoresForFieldValues);
            final Map.Entry<Object, Double> entryWithLowestValue = entryWithLowestValueOpt.get();
            Object bestValue = entryWithLowestValue.getKey();
            final Double bestValueLoss = entryWithLowestValue.getValue();
            currentConfigurationLoss = bestValueLoss;
            logger.info("For field " + fieldName + ", best value is " + bestValue + " with loss " + bestValueLoss);
            currentConfiguration.put(fieldName, bestValue);
        }
        return new ObjectWithLoss<Map<String, Object>>(currentConfiguration, currentConfigurationLoss);
    }

    private Map<Object, Double> getScoresForFieldValues(final Iterable<? extends AbstractInstance> trainingData,
                                                        final Map<String, Object> baselineConfiguration,
                                                        final Map<Map<String, Object>, Double> configurationLoss,
                                                        final String fieldName,
                                                        final FieldValueRecommender fieldValueRecommender) {
        Map<Object, Double> valueLoss = Maps.newHashMap();
        while (true) {
            Optional<Object> valueToTestOpt = fieldValueRecommender.recommendNextValue(valueLoss);
            if (!valueToTestOpt.isPresent()) {
                break;
            }
            final Object valueToTest = valueToTestOpt.get();
            Map<String, Object> configurationToTest = Maps.newHashMap(baselineConfiguration);
            configurationToTest.put(fieldName, valueToTest);
            if (configurationLoss.containsKey(configurationToTest)) {
                double lastLoss = configurationLoss.get(configurationToTest);
                valueLoss.put(valueToTest, lastLoss);
                continue; // No point in testing the same configuration twice
            }
            logger.info("Testing predictive model configuration: " + configurationToTest);
            final PMB predictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(configurationToTest);
            final double crossValidatedLoss = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, trainingData);
            logger.info("Loss for configuration " + configurationToTest + " is " + crossValidatedLoss);
            valueLoss.put(valueToTest, crossValidatedLoss);
            configurationLoss.put(configurationToTest, crossValidatedLoss);
        }
        return valueLoss;
    }

    public static class ObjectWithLoss<O> {
        O object;
        double loss;

        public O get() {
            return object;
        }

        public double getLoss() {
            return loss;
        }

        public ObjectWithLoss(final O object, final double loss) {
            this.object = object;
            this.loss = loss;
        }
    }
}
