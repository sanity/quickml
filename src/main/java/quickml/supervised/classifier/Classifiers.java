package quickml.supervised.classifier;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.ClassifierInstance;
import quickml.supervised.Utils;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierRMSELossFunction;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizerBuilder;
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
public class Classifiers {
    private static final Logger logger = LoggerFactory.getLogger(Classifiers.class);

    public static <T extends ClassifierInstance> Pair<Map<String, Serializable>, RandomDecisionForest>  getOptimizedRandomForest(List<T> trainingData) {
        FoldedData<T> foldedData = new FoldedData<>(trainingData, 10, 2);
        ClassifierLossChecker<T, RandomDecisionForest> classifierInstanceClassifierLossChecker = new ClassifierLossChecker<>(new ClassifierRMSELossFunction());
        RandomDecisionForestBuilder<T> modelBuilder = new RandomDecisionForestBuilder<T>();
        PredictiveModelOptimizer optimizer=  new PredictiveModelOptimizerBuilder<RandomDecisionForest, T>()
                .modelBuilder(modelBuilder)
                .dataCycler(foldedData)
                .lossChecker(classifierInstanceClassifierLossChecker)
                .valuesToTest(Classifiers.createConfig())
                .iterations(3).build();

        Map<String, Serializable> optimalConfig = optimizer.determineOptimalConfig();

        modelBuilder.updateBuilderConfig(optimalConfig);
        return Pair.with(optimalConfig, modelBuilder.buildPredictiveModel(trainingData));
    }

    public static <T extends ClassifierInstance> Pair<Map<String, Serializable>, DownsamplingClassifier>  getOptimizedDownsampledRandomForest(List<T> trainingData, int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor, DownsamplingClassifierBuilder<T> modelBuilder,  Map<String, FieldValueRecommender> config) {
        /**
         * @param rebuildsPerValidation is the number of times the model will be rebuilt with a new training set while estimating the loss of a model
         *                              with a prarticular set of hyperparameters
         * @param fractionOfDataForValidation is the fraction of the training data that out of time validation is performed on during parameter optimization.
         *                                    Note, the final model returned by the method uses all data.
         */

        int timeSliceHours = getTimeSliceHours(trainingData, rebuildsPerValidation, dateTimeExtractor);
        double crossValidationFraction = 0.2;
        TrainingDataCycler<T> outOfTimeData = new OutOfTimeData<T>(trainingData, crossValidationFraction, timeSliceHours, dateTimeExtractor);
        ClassifierLossChecker<T, DownsamplingClassifier> classifierInstanceClassifierLossChecker = new ClassifierLossChecker<>(lossFunction);

        PredictiveModelOptimizer optimizer=  new PredictiveModelOptimizerBuilder<DownsamplingClassifier, T>()
                .modelBuilder(modelBuilder)
                .dataCycler(outOfTimeData)
                .lossChecker(classifierInstanceClassifierLossChecker)
                .valuesToTest(config)
                .iterations(3).build();
        Map<String, Serializable> bestParams =  optimizer.determineOptimalConfig();

        RandomDecisionForestBuilder<T> randomForestBuilder = new RandomDecisionForestBuilder<T>(new DecisionTreeBuilder<T>().attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7))).numTrees(24);
        DownsamplingClassifierBuilder<T> downsamplingClassifierBuilder = new DownsamplingClassifierBuilder<>(randomForestBuilder,0.1);
        downsamplingClassifierBuilder.updateBuilderConfig(bestParams);

        DownsamplingClassifier downsamplingClassifier = downsamplingClassifierBuilder.buildPredictiveModel(trainingData);
        return new Pair<Map<String, Serializable>, DownsamplingClassifier>(bestParams, downsamplingClassifier);
    }

    public static <T extends ClassifierInstance> Pair<Map<String, Serializable>, DownsamplingClassifier>  getOptimizedDownsampledRandomForest(List<T> trainingData, int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor, DownsamplingClassifierBuilder<T> modelBuilder) {
        Map<String, FieldValueRecommender> config = createConfig();
        return getOptimizedDownsampledRandomForest(trainingData,  rebuildsPerValidation, fractionOfDataForValidation, lossFunction, dateTimeExtractor, modelBuilder, config);
    }

    public static <T extends ClassifierInstance> Pair<Map<String, Serializable>, DownsamplingClassifier>  getOptimizedDownsampledRandomForest(List<T> trainingData, int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor,  Map<String, FieldValueRecommender> config) {
        DownsamplingClassifierBuilder<T> modelBuilder = new DownsamplingClassifierBuilder<T>(new RandomDecisionForestBuilder<T>(), .1);
        return getOptimizedDownsampledRandomForest(trainingData,  rebuildsPerValidation, fractionOfDataForValidation, lossFunction, dateTimeExtractor, modelBuilder, config);

    }
    public static Pair<Map<String, Serializable>, DownsamplingClassifier> getOptimizedDownsampledRandomForest(List<? extends ClassifierInstance> trainingData,  int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor) {
        Map<String, FieldValueRecommender> config = createConfig();
        return getOptimizedDownsampledRandomForest(trainingData,  rebuildsPerValidation, fractionOfDataForValidation, lossFunction, dateTimeExtractor, config);
    }

    private static  <I extends ClassifierInstance> int getTimeSliceHours(List<I> trainingData, int rebuildsPerValidation, DateTimeExtractor<I> dateTimeExtractor) {
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
        config.put(MAX_DEPTH.name(), new FixedOrderRecommender(4, 8, 16));//Integer.MAX_VALUE, 2, 3, 5, 6, 9));
        config.put(ATTRIBUTE_VALUE_THRESHOLD_OBSERVATIONS.name(), new FixedOrderRecommender(7, 14));
        config.put(MIN_LEAF_INSTANCES.name(), new FixedOrderRecommender(0, 15));
        config.put(DownsamplingClassifierBuilder.MINORITY_INSTANCE_PROPORTION, new FixedOrderRecommender(.1, .2));
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY.name(), new FixedOrderRecommender(1.0, 0.75));
        return config;
    }

}
