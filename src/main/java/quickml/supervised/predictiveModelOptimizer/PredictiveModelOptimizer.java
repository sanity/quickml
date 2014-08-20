package quickml.supervised.predictiveModelOptimizer;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.collections.MapUtils;
import quickml.supervised.*;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.data.Instance;
import quickml.supervised.PredictiveModelBuilderFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public class PredictiveModelOptimizer<R, P, PM extends PredictiveModel<R, P>, PMB extends PredictiveModelBuilder<R, PM>> {
    private static final Logger logger = LoggerFactory.getLogger(PredictiveModelOptimizer.class);
    private final PredictiveModelBuilderFactory<R, PM, PredictiveModelBuilder<R, PM>> predictiveModelBuilderFactory;
    private final CrossValidator<R, P> crossValidator;
    private final Map<String, FieldValueRecommender> valueRecommenders;
    private final Iterable<Instance<R>> trainingData;
    private Map<Map<String, Object>, Double> configurationLosses = Maps.newHashMap();
    private volatile boolean hasRun = false;
    private static final int MAX_ITERATIONS = 10;
    private int maxIterations;

    public PredictiveModelOptimizer(PredictiveModelBuilderFactory<R, PM, PredictiveModelBuilder<R, PM>> predictiveModelBuilderFactory, final Iterable<Instance<R>> trainingData, CrossValidator<R, P> crossValidator) {
        this(MAX_ITERATIONS, predictiveModelBuilderFactory, trainingData, crossValidator, predictiveModelBuilderFactory.createDefaultParametersToOptimize());
    }

    public PredictiveModelOptimizer(PredictiveModelBuilderFactory<R, PM, PredictiveModelBuilder<R, PM>> predictiveModelBuilderFactory, final Iterable<Instance<R>> trainingData, CrossValidator<R, P> crossValidator, Map<String, FieldValueRecommender> valueRecommenders) {
        this(MAX_ITERATIONS, predictiveModelBuilderFactory, trainingData, crossValidator, valueRecommenders);
    }

    public PredictiveModelOptimizer(int maxIterations, PredictiveModelBuilderFactory<R, PM, PredictiveModelBuilder<R, PM>> predictiveModelBuilderFactory, final Iterable<Instance<R>> trainingData, CrossValidator<R, P> crossValidator, Map<String, FieldValueRecommender> valueRecommenders) {
        this.predictiveModelBuilderFactory = predictiveModelBuilderFactory;
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
            final Optional<Map.Entry<Object, Double>> entryWithLowestValueOpt = MapUtils.getEntryWithLowestValue(scoresForFieldValues);
            final Map.Entry<Object, Double> entryWithLowestValue = entryWithLowestValueOpt.get();
            Object bestValue = entryWithLowestValue.getKey();
            final Double bestValueLoss = entryWithLowestValue.getValue();
            currentConfigurationLoss = bestValueLoss;
            logger.info("For field " + fieldName + ", best value is " + bestValue + " with loss " + bestValueLoss);
            currentConfiguration.put(fieldName, bestValue);
        }
        return new ObjectWithLoss<Map<String, Object>>(currentConfiguration, currentConfigurationLoss);
    }

    private Map<Object, Double> getScoresForFieldValues(final Iterable<Instance<R>> trainingData,
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
            final PredictiveModelBuilder<R, PM> predictiveModelBuilder = predictiveModelBuilderFactory.buildBuilder(configurationToTest);
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