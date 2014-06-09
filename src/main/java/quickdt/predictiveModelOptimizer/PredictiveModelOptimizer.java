package quickdt.predictiveModelOptimizer;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.Misc;
import quickdt.crossValidation.CrossValidator;
import quickdt.crossValidation.StationaryCrossValidator;
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
    private final Iterable<? extends AbstractInstance> trainingData;
    private Map<Map<String, Object>, Double> configurationLosses = Maps.newHashMap();
    private volatile boolean hasRun = false;
    private static final int MAX_ITERATIONS = 10;
    private int maxIterations;
    public PredictiveModelOptimizer(PredictiveModelBuilderBuilder<PM, PMB> predictiveModelBuilderBuilder, final Iterable<? extends AbstractInstance> trainingData) {
        this(predictiveModelBuilderBuilder, trainingData, new StationaryCrossValidator());
    }

    public PredictiveModelOptimizer(PredictiveModelBuilderBuilder<PM, PMB> predictiveModelBuilderBuilder, final Iterable<? extends AbstractInstance> trainingData, CrossValidator crossValidator) {
        this(MAX_ITERATIONS, predictiveModelBuilderBuilder, trainingData, crossValidator, predictiveModelBuilderBuilder.createDefaultParametersToOptimize());
    }

    public PredictiveModelOptimizer(PredictiveModelBuilderBuilder<PM, PMB> predictiveModelBuilderBuilder, final Iterable<? extends AbstractInstance> trainingData, CrossValidator crossValidator, Map<String, FieldValueRecommender> valueRecommenders) {
        this(MAX_ITERATIONS, predictiveModelBuilderBuilder, trainingData, crossValidator, valueRecommenders);
    }

    public PredictiveModelOptimizer(int maxIterations, PredictiveModelBuilderBuilder<PM, PMB> predictiveModelBuilderBuilder, final Iterable<? extends AbstractInstance> trainingData, CrossValidator crossValidator, Map<String, FieldValueRecommender> valueRecommenders) {
        this.predictiveModelBuilderBuilder = predictiveModelBuilderBuilder;
        this.trainingData = trainingData;
        this.crossValidator = crossValidator;
        this.valueRecommenders = valueRecommenders;
        this.maxIterations = maxIterations;
    }

    public Map<String, Object> determineOptimalConfiguration() {
        if (hasRun) {
            throw new IllegalStateException("Can't call this method more than once");
        } else {
            hasRun = true;
        }

        Map<String, Object> startingConfiguration = Maps.newHashMap();
        for (Map.Entry<String, FieldValueRecommender> stringFieldValueRecommenderEntry : valueRecommenders.entrySet()) {
                final Optional<Object> firstValueOptional = stringFieldValueRecommenderEntry.getValue().recommendNextValue(Collections.<Object, Double>emptyMap());
            if (!firstValueOptional.isPresent()) {
                throw new RuntimeException("Failed to retrieve initial value for field " + stringFieldValueRecommenderEntry.getKey());
            }
            startingConfiguration.put(stringFieldValueRecommenderEntry.getKey(), firstValueOptional.get());
        }
        return determineOptimalConfiguration(startingConfiguration);
    }

    public Map<String, Object> determineOptimalConfiguration(Map<String, Object> startingConfiguration) {
        Map<String, Object> bestConfigurationSoFar = startingConfiguration;
        int iterations = 0;
        while( iterations < maxIterations ) {
            final ObjectWithLoss<Map<String, Object>> newBestConfigurationWithLoss = iterateAndImproveConfiguration(bestConfigurationSoFar);
            if (newBestConfigurationWithLoss.get().equals(bestConfigurationSoFar)) {
                logger.info("Best configuration unchanged after iteration, we're done here.  Configuration: " + newBestConfigurationWithLoss.get() + " with loss: " + newBestConfigurationWithLoss.getLoss());
                break;
            } else {
                bestConfigurationSoFar = newBestConfigurationWithLoss.get();
                logger.info("Found new best configuration after iteration: "+bestConfigurationSoFar+" with loss "+newBestConfigurationWithLoss.getLoss());
            }
            iterations++;
        }
        if ( iterations==maxIterations )
            logger.debug("optimizer did not converge");
        return bestConfigurationSoFar;
    }

    private ObjectWithLoss<Map<String, Object>> iterateAndImproveConfiguration(Map<String, Object> startingConfiguration) {
        Map<String, Object> currentConfiguration = Maps.newHashMap(startingConfiguration);
        double currentConfigurationLoss = Double.MAX_VALUE;
        for (Map.Entry<String, FieldValueRecommender> stringFieldValueRecommenderEntry : valueRecommenders.entrySet()) {
            String fieldName = stringFieldValueRecommenderEntry.getKey();
            logger.info("Optimizing field '" + fieldName + "'");
            final Map<Object, Double> scoresForFieldValues = getScoresForFieldValues(trainingData, currentConfiguration, fieldName, stringFieldValueRecommenderEntry.getValue());
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
            if (configurationLosses.containsKey(configurationToTest)) {
                double lastLoss = configurationLosses.get(configurationToTest);
                valueLoss.put(valueToTest, lastLoss);
                continue; // No point in testing the same configuration twice
            }
            logger.info("Testing predictive model configuration: " + configurationToTest);
            final PMB predictiveModelBuilder = predictiveModelBuilderBuilder.buildBuilder(configurationToTest);
            final double crossValidatedLoss = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, trainingData);
            logger.info("Loss for configuration " + configurationToTest + " is " + crossValidatedLoss);
            valueLoss.put(valueToTest, crossValidatedLoss);
            configurationLosses.put(configurationToTest, crossValidatedLoss);
        }
        return valueLoss;
    }

    public static class ObjectWithLoss<O> {
        O object;
        double loss;

        public ObjectWithLoss(final O object, final double loss) {
            this.object = object;
            this.loss = loss;
        }

        public O get() {
            return object;
        }

        public double getLoss() {
            return loss;
        }
    }
}
