package quickml.supervised.classifier;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.ClassifierInstance;
import quickml.supervised.Utils;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizerBuilder;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static quickml.supervised.classifier.decisionTree.TreeBuilder.*;

/**
 * Created by alexanderhawk on 3/5/15.
 */
public class Classifiers {
    private static final Logger logger = LoggerFactory.getLogger(Classifiers.class);

    public static <T extends ClassifierInstance> Pair<Map<String, Object>, DownsamplingClassifier>  getOptimizedDownsampledRandomForest(List<T> trainingData, int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor, DownsamplingClassifierBuilder<T> modelBuilder,  Map<String, FieldValueRecommender> config) {
        /**
         * @param rebuildsPerValidation is the number of times the model will be rebuilt with a new training set while estimating the loss of a model
         *                              with a prarticular set of hyperparameters
         * @param fractionOfDataForValidation is the fraction of the training data that out of time validation is performed on during parameter optimization.
         *                                    Note, the final model returned by the method uses all data.
         */

        int timeSliceHours = getTimeSliceHours(trainingData, rebuildsPerValidation, dateTimeExtractor);
        double crossValidationFraction = 0.2;
        TrainingDataCycler<T> outOfTimeData = new OutOfTimeData<T>(trainingData, crossValidationFraction, timeSliceHours, dateTimeExtractor);
        ClassifierLossChecker<T> classifierInstanceClassifierLossChecker = new ClassifierLossChecker<>(lossFunction);

        PredictiveModelOptimizer optimizer=  new PredictiveModelOptimizerBuilder<Classifier, T>()
                        .modelBuilder(modelBuilder)
                        .dataCycler(outOfTimeData)
                        .lossChecker(classifierInstanceClassifierLossChecker)
                        .valuesToTest(config)
                        .iterations(3).build();
        Map<String, Object> bestParams =  optimizer.determineOptimalConfig();

        RandomForestBuilder<T> randomForestBuilder = new RandomForestBuilder<T>(new TreeBuilder<T>().attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7))).numTrees(24);
        DownsamplingClassifierBuilder<T> downsamplingClassifierBuilder = new DownsamplingClassifierBuilder<>(randomForestBuilder,0.1);
        downsamplingClassifierBuilder.updateBuilderConfig(bestParams);

        DownsamplingClassifier downsamplingClassifier = downsamplingClassifierBuilder.buildPredictiveModel(trainingData);
        return new Pair<Map<String, Object>, DownsamplingClassifier>(bestParams, downsamplingClassifier);
    }

    public static <T extends ClassifierInstance> Pair<Map<String, Object>, DownsamplingClassifier>  getOptimizedDownsampledRandomForest(List<T> trainingData, int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor, DownsamplingClassifierBuilder<T> modelBuilder, Set<String> exemptAttributes) {
        Map<String, FieldValueRecommender> config = createConfig(exemptAttributes);
        return getOptimizedDownsampledRandomForest(trainingData,  rebuildsPerValidation, fractionOfDataForValidation, lossFunction, dateTimeExtractor, modelBuilder, config);
    }

    public static <T extends ClassifierInstance> Pair<Map<String, Object>, DownsamplingClassifier>  getOptimizedDownsampledRandomForest(List<T> trainingData, int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor,  Map<String, FieldValueRecommender> config) {
        DownsamplingClassifierBuilder<T> modelBuilder = new DownsamplingClassifierBuilder<T>(new RandomForestBuilder<T>(), .1);
        return getOptimizedDownsampledRandomForest(trainingData,  rebuildsPerValidation, fractionOfDataForValidation, lossFunction, dateTimeExtractor, modelBuilder, config);

    }
        public static Pair<Map<String, Object>, DownsamplingClassifier> getOptimizedDownsampledRandomForest(List<? extends ClassifierInstance> trainingData,  int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor, Set<String> exemptAttributes) {
        Map<String, FieldValueRecommender> config = createConfig(exemptAttributes);
        return getOptimizedDownsampledRandomForest(trainingData,  rebuildsPerValidation, fractionOfDataForValidation, lossFunction, dateTimeExtractor, config);
    }

    private static int getTimeSliceHours(List<? extends ClassifierInstance> trainingData, int rebuildsPerValidation, DateTimeExtractor<ClassifierInstance> dateTimeExtractor) {

        Utils.sortTrainingInstancesByTime(trainingData, dateTimeExtractor);
        DateTime latestDateTime = dateTimeExtractor.extractDateTime(trainingData.get(trainingData.size()-1));
        int indexOfEarliestValidationInstance = (int) (0.8 * trainingData.size()) - 1;
        DateTime earliestValidationTime = dateTimeExtractor.extractDateTime(trainingData.get(indexOfEarliestValidationInstance));
        Duration duration = new Duration(earliestValidationTime, latestDateTime);
        int validationPeriodHours = (int)duration.getStandardHours();
        return validationPeriodHours/rebuildsPerValidation;
    }


    private static  Map<String, FieldValueRecommender> createConfig(Set<String> exemptAttributes) {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        config.put(MAX_DEPTH, new FixedOrderRecommender(4, 8, 12));//Integer.MAX_VALUE, 2, 3, 5, 6, 9));
        config.put(MIN_OCCURRENCES_OF_ATTRIBUTE_VALUE, new FixedOrderRecommender(7, 10));
        config.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 15));
        config.put(DownsamplingClassifierBuilder.MINORITY_INSTANCE_PROPORTION, new FixedOrderRecommender(.1, .25));
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY, new FixedOrderRecommender(1.0, 0.75));
        config.put(ORDINAL_TEST_SPLITS, new FixedOrderRecommender(5, 7));
        config.put(MIN_SPLIT_FRACTION, new FixedOrderRecommender(0.01, 0.25, .5 ));
        config.put(EXEMPT_ATTRIBUTES, new FixedOrderRecommender(exemptAttributes));
        return config;
    }

}
