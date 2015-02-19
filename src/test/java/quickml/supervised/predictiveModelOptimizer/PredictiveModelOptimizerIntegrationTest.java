package quickml.supervised.predictiveModelOptimizer;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.InstanceLoader;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.scorers.InformationGainScorer;
import quickml.supervised.classifier.decisionTree.scorers.MSEScorer;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;
import quickml.supervised.crossValidation.lossfunctions.ClassifierRMSELossFunction;
import quickml.supervised.crossValidation.lossfunctions.NonWeightedAUCCrossValLossFunction;
import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.MonotonicConvergenceRecommender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static quickml.supervised.InstanceLoader.getAdvertisingInstances;
import static quickml.supervised.classifier.decisionTree.TreeBuilder.*;
import static quickml.supervised.classifier.decisionTree.scorers.MSEScorer.CrossValidationCorrection.FALSE;
import static quickml.supervised.classifier.decisionTree.scorers.MSEScorer.CrossValidationCorrection.TRUE;
import static quickml.supervised.classifier.randomForest.RandomForestBuilder.NUM_TREES;

public class PredictiveModelOptimizerIntegrationTest {


    private PredictiveModelOptimizer optimizer;

    @Before
    public void setUp() throws Exception {
        List<ClassifierInstance> advertisingInstances = getAdvertisingInstances();
        advertisingInstances = advertisingInstances.subList(0, 3000);
        optimizer = new PredictiveModelOptimizerBuilder<Classifier, ClassifierInstance>()
                .modelBuilder(new RandomForestBuilder<>())
                .dataCycler(new OutOfTimeData<>(advertisingInstances, 0.2, 12, new OnespotDateTimeExtractor()))
                .lossChecker(new ClassifierLossChecker<>(new ClassifierLogCVLossFunction(.000001)))//ClassifierLogCVLossFunction(0.000001)))
                        .valuesToTest(createConfig())
                        .iterations(7)
                        .build();
    }


    @Test
    public void testOptimizer() throws Exception {
        System.out.println("optimalConfig = " + optimizer.determineOptimalConfig());
    }


    private Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        List<Integer> trialNumTrees = Arrays.asList(5, 10, 20);
        config.put(NUM_TREES, new MonotonicConvergenceRecommender(trialNumTrees));
        config.put(IGNORE_ATTR_PROB, new FixedOrderRecommender(0.1, 0.4, 0.7));
       config.put(MAX_DEPTH, new FixedOrderRecommender(16));//Integer.MAX_VALUE, 2, 3, 5, 6, 9));
        config.put(MIN_SCORE, new FixedOrderRecommender(0.00000000000001));//, Double.MIN_VALUE, 0.0, 0.000001, 0.0001, 0.001, 0.01, 0.1));
        config.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(2, 7, 11));
        config.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 20));
        config.put(SCORER, new FixedOrderRecommender(new InformationGainScorer(), new GiniImpurityScorer()));
        config.put(PENALIZE_CATEGORICAL_SPLITS, new FixedOrderRecommender(true, false));
        return config;
    }




}
