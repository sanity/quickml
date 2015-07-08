package quickml.supervised.classifier;

import com.google.common.collect.Maps;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.Utils;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizerBuilder;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

import static quickml.supervised.tree.constants.ForestOptions.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/5/15.
 */
public class Classifiers {
    private static final Logger logger = LoggerFactory.getLogger(Classifiers.class);

    public static <I extends ClassifierInstance> Pair<Map<String, Serializable>, DownsamplingClassifier> getOptimizedDownsampledRandomForest(List<I> trainingData, int rebuildsPerValidation, double fractionOfDataForValidation,
                                                                                                                                       ClassifierLossFunction lossFunction, DateTimeExtractor<I> dateTimeExtractor,  Map<String, FieldValueRecommender> config) {
        /**
         * @param rebuildsPerValidation is the number of times the model will be rebuilt with a new training set while estimating the loss of a model
         *                              with a prarticular set of hyperparameters
         * @param fractionOfDataForValidation is the fraction of the training data that out of time validation is performed on during parameter optimization.
         *                                    Note, the final model returned by the method uses all data.
         */

        int timeSliceHours = getTimeSliceHours(trainingData, rebuildsPerValidation, dateTimeExtractor);
        double crossValidationFraction = 0.2;
        PredictiveModelOptimizer optimizer=  new PredictiveModelOptimizerBuilder<RandomDecisionForest,I>()
                        .modelBuilder(new RandomDecisionForestBuilder<>())
                        .dataCycler(new OutOfTimeData<I>(trainingData, crossValidationFraction, timeSliceHours,dateTimeExtractor))
                        .lossChecker(new ClassifierLossChecker<I, RandomDecisionForest>(lossFunction))
                        .valuesToTest(config)
                        .iterations(3).build();
        Map<String, Serializable> bestParams =  optimizer.determineOptimalConfig();

        RandomDecisionForestBuilder<I> randomDecisionForestBuilder = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<I>().maxDepth(5).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7))).numTrees(24);
        DownsamplingClassifierBuilder<I> downsamplingClassifierBuilder = new DownsamplingClassifierBuilder<I>(randomDecisionForestBuilder,0.1);
        downsamplingClassifierBuilder.updateBuilderConfig(bestParams);

        DownsamplingClassifier downsamplingClassifier = downsamplingClassifierBuilder.buildPredictiveModel(trainingData);
        return new Pair<Map<String, Serializable>, DownsamplingClassifier>(bestParams, downsamplingClassifier);
    }
    public static <I extends ClassifierInstance> Pair<Map<String, Serializable>, DownsamplingClassifier> getOptimizedDownsampledRandomForest(List<I> trainingData,  int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction, DateTimeExtractor dateTimeExtractor) {
        Map<String, FieldValueRecommender> config = createConfig();
        return getOptimizedDownsampledRandomForest(trainingData,  rebuildsPerValidation, fractionOfDataForValidation, lossFunction, dateTimeExtractor, config);
    }

    private static <I extends ClassifierInstance> int getTimeSliceHours(List<I> trainingData, int rebuildsPerValidation, DateTimeExtractor<I> dateTimeExtractor) {

        Utils.sortTrainingInstancesByTime(trainingData, dateTimeExtractor);
        DateTime latestDateTime = dateTimeExtractor.extractDateTime(trainingData.get(trainingData.size()-1));
        int indexOfEarliestValidationInstance = (int) (0.8 * trainingData.size()) - 1;
        DateTime earliestValidationTime = dateTimeExtractor.extractDateTime(trainingData.get(indexOfEarliestValidationInstance));
        Period period = new Period(earliestValidationTime, latestDateTime);
        int validationPeriodHours = period.getHours();
        return validationPeriodHours/rebuildsPerValidation;
    }


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
