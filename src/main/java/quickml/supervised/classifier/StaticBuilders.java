package quickml.supervised.classifier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.scorers.InformationGainScorer;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;
import quickml.supervised.crossValidation.lossfunctions.LossFunction;
import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizerBuilder;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.MonotonicConvergenceRecommender;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static quickml.supervised.classifier.decisionTree.TreeBuilder.*;
import static quickml.supervised.classifier.randomForest.RandomForestBuilder.NUM_TREES;

/**
 * Created by alexanderhawk on 3/5/15.
 */
public class StaticBuilders {
    private static final Logger logger = LoggerFactory.getLogger(StaticBuilders.class);

    public static DownsamplingClassifier getOptimizedDownsampledRandomForest(List<ClassifierInstance> trainingData, int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction,  Map<String, FieldValueRecommender> config) {
        /**
         * @param rebuildsPerValidation is the number of times the model will be rebuilt with a new training set while estimating the loss of a model
         *                              with a prarticular set of hyperparameters
         * @param fractionOfDataForValidation is the fraction of the training data that out of time validation is performed on during parameter optimization.
         *                                    Note, the final model returned by the method uses all data.
         */

        int timeSliceHours = getTimeSliceHours(trainingData, rebuildsPerValidation);
        PredictiveModelOptimizer optimizer=  new PredictiveModelOptimizerBuilder<Classifier, ClassifierInstance>()
                        .modelBuilder(new RandomForestBuilder<>())
                        .dataCycler(new OutOfTimeData<>(trainingData, 0.2, timeSliceHours, new OnespotDateTimeExtractor()))
                        .lossChecker(new ClassifierLossChecker<>(lossFunction))
                        .valuesToTest(config)
                        .iterations(3).build();
        Map<String, Object> bestParams =  optimizer.determineOptimalConfig();

        RandomForestBuilder<ClassifierInstance> randomForestBuilder = new RandomForestBuilder<>(new TreeBuilder<>().ignoreAttributeAtNodeProbability(0.7)).numTrees(24);
        DownsamplingClassifierBuilder<ClassifierInstance> downsamplingClassifierBuilder = new DownsamplingClassifierBuilder<>(randomForestBuilder,0.1);
        downsamplingClassifierBuilder.updateBuilderConfig(bestParams);

        return downsamplingClassifierBuilder.buildPredictiveModel(trainingData);

    }
    public static DownsamplingClassifier getOptimizedDownsampledRandomForest(List<ClassifierInstance> trainingData,  int rebuildsPerValidation, double fractionOfDataForValidation, ClassifierLossFunction lossFunction) {
        Map<String, FieldValueRecommender> config = createConfig();
        return getOptimizedDownsampledRandomForest(trainingData,  rebuildsPerValidation, fractionOfDataForValidation, lossFunction, config);
    }

    private static int getTimeSliceHours(List<ClassifierInstance> trainingData, int rebuildsPerValidation) {
        final DateTimeExtractor<ClassifierInstance> dateTimeExtractor = new OnespotDateTimeExtractor();
        Collections.sort(trainingData, new Comparator<ClassifierInstance>() {
            @Override
            public int compare(ClassifierInstance o1, ClassifierInstance o2) {
                DateTime dateTime1 = dateTimeExtractor.extractDateTime(o1);
                DateTime dateTime2 = dateTimeExtractor.extractDateTime(o2);
                return dateTime1.compareTo(dateTime2);
            }
        });
        DateTime latestDateTime = dateTimeExtractor.extractDateTime(trainingData.get(trainingData.size()-1));
        DateTime earliestValidationTime = dateTimeExtractor.extractDateTime(trainingData.get((int)(0.8*trainingData.size())-1));
        Period period = new Period(earliestValidationTime, latestDateTime);
        int validationPeriodHours = period.getHours();
        return validationPeriodHours/rebuildsPerValidation;
    }

    private static  Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        config.put(MAX_DEPTH, new FixedOrderRecommender(4, 8, 16));//Integer.MAX_VALUE, 2, 3, 5, 6, 9));
        config.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(7, 14));
        config.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 15));
        config.put(DownsamplingClassifierBuilder.MINORITY_INSTANCE_PROPORTION, new FixedOrderRecommender(.1, .2));
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY, new FixedOrderRecommender(1.0, 0.75));
        return config;
    }

}
