package quickml.supervised.classifier;

import com.google.common.collect.Maps;
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
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickml.supervised.predictiveModelOptimizer.PredictiveModelOptimizerBuilder;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.MonotonicConvergenceRecommender;

import java.io.Serializable;
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

    public DownsamplingClassifier getOptimizedDownsampledRandomForest(List<ClassifierInstance> trainingData, Map<String, FieldValueRecommender> config, int timeSliceHours) {
        PredictiveModelOptimizer optimizer=  new PredictiveModelOptimizerBuilder<Classifier, ClassifierInstance>()
                        .modelBuilder(new RandomForestBuilder<>())
                        .dataCycler(new OutOfTimeData<>(trainingData, 0.2, timeSliceHours, new OnespotDateTimeExtractor()))
                        .lossChecker(new ClassifierLossChecker<>(new ClassifierLogCVLossFunction(0.000001)))
                        .valuesToTest(config)
                        .iterations(3).build();
        Map<String, Object> bestParams =  optimizer.determineOptimalConfig();

        RandomForestBuilder<ClassifierInstance> randomForestBuilder = new RandomForestBuilder<>(new TreeBuilder<>().ignoreAttributeAtNodeProbability(0.7)).numTrees(24);
        DownsamplingClassifierBuilder<ClassifierInstance> downsamplingClassifierBuilder = new DownsamplingClassifierBuilder<>(randomForestBuilder,0);
        downsamplingClassifierBuilder.updateBuilderConfig(bestParams);

        return downsamplingClassifierBuilder.buildPredictiveModel(trainingData);

    }
    public DownsamplingClassifier getOptimizedDownsampledRandomForest(List<ClassifierInstance> trainingData, int rebuildsPerValidation) {
        Map<String, FieldValueRecommender> config = createConfig();
        //make timeSlice Hours correct for getting the numRebuilds by assessing the training data
        return getOptimizedDownsampledRandomForest(trainingData, timeSliceHours);
    }

    private Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        config.put(MAX_DEPTH, new FixedOrderRecommender(4, 8, 16));//Integer.MAX_VALUE, 2, 3, 5, 6, 9));
        config.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(7, 14));
        config.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 15));
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY, new FixedOrderRecommender(1.0, 0.75));
        return config;
    }

}
