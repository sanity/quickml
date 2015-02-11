package quickml.supervised.alternative.optimizer;

import com.beust.jcommander.internal.Maps;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import quickml.supervised.InstanceLoader;
import quickml.supervised.alternative.crossValidationLoss.ClassifierLossChecker;
import quickml.supervised.alternative.crossValidationLoss.ClassifierRMSELossFunction;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.scorers.InformationGainScorer;
import quickml.supervised.classifier.decisionTree.scorers.MSEScorer;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.decisionTree.TreeBuilder.*;
import static quickml.supervised.classifier.decisionTree.scorers.MSEScorer.CrossValidationCorrection.FALSE;
import static quickml.supervised.classifier.decisionTree.scorers.MSEScorer.CrossValidationCorrection.TRUE;
import static quickml.supervised.classifier.randomForest.RandomForestBuilder.NUM_TREES;

public class PredictiveModelOptimizer2IntegrationTest {


    private PredictiveModelOptimizer2 optimizer;

    @Before
    public void setUp() throws Exception {
        List<ClassifierInstance> trainingInstances = InstanceLoader.getAdvertisingInstances();
        TrainingDataCycler<ClassifierInstance> outOfTimeData = new OutOfTimeData<>(trainingInstances, 0.5, 12, new OnespotDateTimeExtractor());
        ClassifierLossChecker lossChecker = new ClassifierLossChecker(new ClassifierRMSELossFunction());
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder();

        CrossValidator<Classifier, ClassifierInstance> crossValidator = new CrossValidator<>(randomForestBuilder, lossChecker, outOfTimeData);
        optimizer = new PredictiveModelOptimizer2(createConfig(), crossValidator, 3);
    }


    @Test
    public void testOptimizer() throws Exception {

        Map<String, Object> optimalConfig = optimizer.determineOptimalConfig();
        System.out.println("optimalConfig = " + optimalConfig);
    }


    private Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        config.put(NUM_TREES, new FixedOrderRecommender(5, 10));
        config.put(IGNORE_ATTR_PROB, new FixedOrderRecommender(0.5, 0.0, 0.1, 0.2, 0.4, 0.7, 0.8, 0.9, 0.95, 0.99));
        config.put(MAX_DEPTH, new FixedOrderRecommender(Integer.MAX_VALUE, 2, 3, 5, 6, 9));
        config.put(MIN_SCORE, new FixedOrderRecommender(0.00000000000001, Double.MIN_VALUE, 0.0, 0.000001, 0.0001, 0.001, 0.01, 0.1));
        config.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(10, 20, 30, 40));
        config.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 10, 100));
        config.put(SCORER, new FixedOrderRecommender(new MSEScorer(FALSE), new MSEScorer(TRUE), new SplitDiffScorer(), new InformationGainScorer(), new GiniImpurityScorer()));
        config.put(PENALIZE_CATEGORICAL_SPLITS, new FixedOrderRecommender(true, false));
        return config;
    }




}
