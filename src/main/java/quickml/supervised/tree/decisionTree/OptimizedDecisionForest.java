package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.LossChecker;
import quickml.supervised.crossValidation.RegressionLossChecker;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickml.supervised.predictiveModelOptimizer.SimplePredictiveModelOptimizerBuilder;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static quickml.supervised.tree.constants.ForestOptions.*;
import static quickml.supervised.tree.constants.ForestOptions.NUM_TREES;


/**
 * Created by alexanderhawk on 3/5/15.
 */

/* FIXME: This is unnecessarily specialized to out-of-time cross-validation, should be generalized
 *        so that it can support alternate ways to separate training from test set (for example,
 *        any Comparable class can be used to sort the training instances, not just DateTime).
 */
public class OptimizedDecisionForest {
    private static final Logger logger = LoggerFactory.getLogger(quickml.supervised.tree.regressionTree.OptimizedRegressionForests.class);

    public static <T extends ClassifierInstance> Pair<Map<String, Serializable>, RandomDecisionForest> getOptimizedRandomForest(List<T> trainingData, Map<String, FieldValueRecommender> config) {
        TrainingDataCycler<T> dataCycler = new FoldedData<>(trainingData, 6, 2);
        return getOptimizedRandomForest(trainingData, config, dataCycler);
    }

    public static <T extends ClassifierInstance> Pair<Map<String, Serializable>, RandomDecisionForest> getOptimizedRandomForest(List<T> trainingData, Map<String, FieldValueRecommender> config, TrainingDataCycler<T> trainingDataCycler) {
        ClassifierLossChecker<T, RandomDecisionForest> lossChecker = new ClassifierLossChecker<>(new WeightedAUCCrossValLossFunction(1.0));
        RandomDecisionForestBuilder<T> modelBuilder = new RandomDecisionForestBuilder<T>();
        PredictiveModelOptimizer optimizer = new SimplePredictiveModelOptimizerBuilder<RandomDecisionForest, T>()
                .modelBuilder(modelBuilder)
                .dataCycler(trainingDataCycler)
                .lossChecker(lossChecker)
                .valuesToTest(config)
                .iterations(2).build();

        Map<String, Serializable> optimalConfig = optimizer.determineOptimalConfig();

        modelBuilder.updateBuilderConfig(optimalConfig);
        return Pair.with(optimalConfig, modelBuilder.buildPredictiveModel(trainingData));
    }


    public static <T extends ClassifierInstance> Pair<Map<String, Serializable>, RandomDecisionForest> getOptimizedRandomForest(List<T> trainingData) {
        Map<String, FieldValueRecommender> config = createConfig();
        return getOptimizedRandomForest(trainingData, config, new FoldedData<>(trainingData, 6, 2));
    }


    private static <I extends ClassifierInstance> int getTimeSliceHours(List<I> trainingData, int rebuildsPerValidation, DateTimeExtractor<I> dateTimeExtractor) {
        Utils.sortTrainingInstancesByTime(trainingData, dateTimeExtractor);
        DateTime latestDateTime = dateTimeExtractor.extractDateTime(trainingData.get(trainingData.size() - 1));
        int indexOfEarliestValidationInstance = (int) (0.8 * trainingData.size()) - 1;
        DateTime earliestValidationTime = dateTimeExtractor.extractDateTime(trainingData.get(indexOfEarliestValidationInstance));
        Duration duration = new Duration(earliestValidationTime, latestDateTime);
        int validationPeriodHours = (int) duration.getStandardHours();
        return validationPeriodHours / rebuildsPerValidation;
    }


    // FIXME: Since most users of QuickML will be content with a default set of hyperparameters, we shouldn't force

    private static Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        config.put(MAX_DEPTH.name(), new FixedOrderRecommender(2, 5, 12));
        config.put(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name(), new FixedOrderRecommender(7, 14));
        config.put(MIN_LEAF_INSTANCES.name(), new FixedOrderRecommender(5));// 10));
        config.put(ATTRIBUTE_IGNORING_STRATEGY.name(), new FixedOrderRecommender(
                new IgnoreAttributesWithConstantProbability(0.75),
                new IgnoreAttributesWithConstantProbability(0.85),
                new IgnoreAttributesWithConstantProbability(0.9)
        ));
        config.put(MIN_SLPIT_FRACTION.name(), new FixedOrderRecommender(0.0));//, 0.05, 0.2));
        config.put(NUM_NUMERIC_BINS.name(), new FixedOrderRecommender(2));//, 5, 8));
        config.put(NUM_SAMPLES_PER_NUMERIC_BIN.name(), new FixedOrderRecommender(25));
//        config.put(DownsamplingClassifierBuilder.MINORITY_INSTANCE_PROPORTION, new FixedOrderRecommender(.1, .2));
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY.name(), new FixedOrderRecommender(1.0, 0.75));
        config.put(NUM_TREES.name(), new FixedOrderRecommender(8));
        return config;
    }
}