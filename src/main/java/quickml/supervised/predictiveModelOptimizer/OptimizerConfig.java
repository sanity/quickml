package quickml.supervised.predictiveModelOptimizer;

import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.scorers.InformationGainScorer;
import quickml.supervised.classifier.decisionTree.scorers.MSEScorer;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.regressionModel.LinearRegression.RidgeLinearModelBuilder;

import java.util.HashMap;
import java.util.Map;

import static quickml.supervised.classifier.decisionTree.TreeBuilder.*;
import static quickml.supervised.classifier.downsampling.DownsamplingClassifierBuilder.MINORITY_INSTANCE_PROPORTION;
import static quickml.supervised.classifier.randomForest.RandomForestBuilder.NUM_TREES;
import static quickml.supervised.classifier.temporallyWeightClassifier.TemporallyReweightedClassifierBuilder.HALF_LIFE_OF_NEGATIVE;
import static quickml.supervised.classifier.temporallyWeightClassifier.TemporallyReweightedClassifierBuilder.HALF_LIFE_OF_POSITIVE;

public class OptimizerConfig {



    public static Map<String, FieldValueRecommender> treeBuilderConfig() {
        HashMap<String, FieldValueRecommender> config = new HashMap<>();
        config.put(IGNORE_ATTR_PROB, new FixedOrderRecommender(0.5, 0.0, 0.1, 0.2, 0.4, 0.7, 0.8, 0.9, 0.95, 0.98, 0.99));
        config.put(MAX_DEPTH, new FixedOrderRecommender(Integer.MAX_VALUE, 2, 3, 4, 5, 6, 7, 9));
        config.put(MIN_SCORE, new FixedOrderRecommender(0.00000000000001, Double.MIN_VALUE, 0.0, 0.000001, 0.0001, 0.001, 0.01, 0.1));
        config.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(5, 0, 1, 64, 1024, 4098));
        config.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 10, 100, 1000, 10000, 100000));
        config.put(SCORER, new FixedOrderRecommender(new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE), new MSEScorer(MSEScorer.CrossValidationCorrection.TRUE), new SplitDiffScorer(), new InformationGainScorer(), new GiniImpurityScorer()));
        config.put(PENALIZE_CATEGORICAL_SPLITS, new FixedOrderRecommender(true, false));
        return config;
    }

    public static Map<String, FieldValueRecommender> randomForestConfig() {
        HashMap<String, FieldValueRecommender> config = new HashMap<>();
        config.put(NUM_TREES, new FixedOrderRecommender(5, 10, 20, 40));
        return config;
    }

    public static Map<String, FieldValueRecommender> ridgeLinearConfig() {
        HashMap<String, FieldValueRecommender> config = new HashMap<>();

        config.put(RidgeLinearModelBuilder.REGULARIZATION_CONSTANT, new FixedOrderRecommender(0.001, 0.003, .01, 0.03, 0.1, 0.3));
        return config;
    }

    public static Map<String, FieldValueRecommender> temporallyReweightedClassifierConfig() {
        HashMap<String, FieldValueRecommender> config = new HashMap<>();
        config.put(HALF_LIFE_OF_NEGATIVE, new FixedOrderRecommender(1.0, 7.0, 30.0));
        config.put(HALF_LIFE_OF_POSITIVE, new FixedOrderRecommender(1.0, 7.0, 30.0));
        return config;
    }

    public static Map<String, FieldValueRecommender> downsamplingClassifiedConfig() {
        HashMap<String, FieldValueRecommender> config = new HashMap<>();
        config.put(MINORITY_INSTANCE_PROPORTION, new FixedOrderRecommender(0.1, 0.2, 0.3, 0.4, 0.5));
        return config;
    }



}
