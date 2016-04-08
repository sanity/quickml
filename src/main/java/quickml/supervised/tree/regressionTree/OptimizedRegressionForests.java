package quickml.supervised.tree.regressionTree;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.Utils;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.RegressionLossChecker;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierRMSELossFunction;
import quickml.supervised.crossValidation.lossfunctions.regressionLossFunctions.RegressionRMSELossFunction;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

import quickml.supervised.ensembles.randomForest.randomRegressionForest.RandomRegressionForest;
import quickml.supervised.ensembles.randomForest.randomRegressionForest.RandomRegressionForestBuilder;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickml.supervised.predictiveModelOptimizer.SimplePredictiveModelOptimizerBuilder;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static quickml.supervised.tree.constants.ForestOptions.*;

/**
 * Created by alexanderhawk on 3/5/15.
 */

/* FIXME: This is unnecessarily specialized to out-of-time cross-validation, should be generalized
 *        so that it can support alternate ways to separate training from test set (for example,
 *        any Comparable class can be used to sort the training instances, not just DateTime).
 */
public class OptimizedRegressionForests {
    private static final Logger logger = LoggerFactory.getLogger(OptimizedRegressionForests.class);

    public static <T extends RegressionInstance> Pair<Map<String, Serializable>, RandomRegressionForest>  getOptimizedRandomForest(List<T> trainingData,  Map<String, FieldValueRecommender> config) {
        FoldedData<T> foldedData = new FoldedData<>(trainingData, 6, 2);
        RegressionLossChecker<RandomRegressionForest, T> lossChecker = new RegressionLossChecker<>(new RegressionRMSELossFunction());
        RandomRegressionForestBuilder<T> modelBuilder = new RandomRegressionForestBuilder<T>();
        PredictiveModelOptimizer optimizer=  new SimplePredictiveModelOptimizerBuilder<RandomRegressionForest, T>()
                .modelBuilder(modelBuilder)
                .dataCycler(foldedData)
                .lossChecker(lossChecker)
                .valuesToTest(config)
                .iterations(2).build();

        Map<String, Serializable> optimalConfig = optimizer.determineOptimalConfig();

        modelBuilder.updateBuilderConfig(optimalConfig);
        return Pair.with(optimalConfig, modelBuilder.buildPredictiveModel(trainingData));
    }
    public static <T extends RegressionInstance> Pair<Map<String, Serializable>, RandomRegressionForest>  getOptimizedRandomForest(List<T> trainingData) {
        Map<String, FieldValueRecommender> config = OptimizedRegressionForests.createConfig();
        return getOptimizedRandomForest(trainingData, config);
    }





    private static  <I extends RegressionInstance> int getTimeSliceHours(List<I> trainingData, int rebuildsPerValidation, DateTimeExtractor<I> dateTimeExtractor) {
        Utils.sortTrainingInstancesByTime(trainingData, dateTimeExtractor);
        DateTime latestDateTime = dateTimeExtractor.extractDateTime(trainingData.get(trainingData.size()-1));
        int indexOfEarliestValidationInstance = (int) (0.8 * trainingData.size()) - 1;
        DateTime earliestValidationTime = dateTimeExtractor.extractDateTime(trainingData.get(indexOfEarliestValidationInstance));
        Duration duration = new Duration(earliestValidationTime, latestDateTime);
        int validationPeriodHours = (int)duration.getStandardHours();
        return validationPeriodHours/rebuildsPerValidation;
    }



    // FIXME: Since most users of QuickML will be content with a default set of hyperparameters, we shouldn't force

    private static  Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        config.put(MAX_DEPTH.name(), new FixedOrderRecommender(12));//Integer.MAX_VALUE, 2, 3, 5, 6, 9));
      //  config.put(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name(), new FixedOrderRecommender(7, 14));
        config.put(MIN_LEAF_INSTANCES.name(), new FixedOrderRecommender(5));// 10));
        config.put(ATTRIBUTE_IGNORING_STRATEGY.name(), new FixedOrderRecommender(
   //             new IgnoreAttributesWithConstantProbability(0.65),
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

//        .degreeOfGainRatioPenalty(1.0)
//                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.85))
//                .maxDepth(12)
//                .minLeafInstances(5)
//                .minSplitFraction(0.1)
//                .numNumericBins(6)
//                .numSamplesPerNumericBin(50)
    }

}
