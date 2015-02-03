package quickml.supervised.alternative.optimizer;

import com.beust.jcommander.internal.Maps;
import org.junit.Before;
import org.junit.Test;
import quickml.supervised.alternative.crossValidationLoss.ClassifierLossChecker;
import quickml.supervised.alternative.crossValidationLoss.ClassifierRMSELossFunction;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.scorers.InformationGainScorer;
import quickml.supervised.classifier.decisionTree.scorers.MSEScorer;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.decisionTree.TreeBuilder.*;
import static quickml.supervised.classifier.decisionTree.scorers.MSEScorer.CrossValidationCorrection.FALSE;
import static quickml.supervised.classifier.decisionTree.scorers.MSEScorer.CrossValidationCorrection.TRUE;

public class PredictiveModelOptimizer2IntegrationTest {


    private PredictiveModelOptimizer2 optimizer;

    @Before
    public void setUp() throws Exception {
        List<ClassifierInstance> trainingInstances = TreeBuilderTestUtils.getInstancesOneEveryHour(1000);
        TrainingDataCycler<ClassifierInstance> outOfTimeData = new OutOfTimeData<>(trainingInstances, 0.5, 72);
        ClassifierLossChecker lossChecker = new ClassifierLossChecker(new ClassifierRMSELossFunction());

        ModelTester<Classifier, ClassifierInstance> modelTester = new ModelTester<>(new TreeBuilder(), lossChecker, outOfTimeData);
        optimizer = new PredictiveModelOptimizer2(createConfig(), modelTester);
    }

    @Test
    public void testOptimizer() throws Exception {
        Map<String, Object> optimalConfig = optimizer.determineOptimalConfig();
        System.out.println("optimalConfig = " + optimalConfig);
    }


    private Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        config.put(IGNORE_ATTR_PROB, new FixedOrderRecommender(0.5, 0.0, 0.1, 0.2, 0.4, 0.7, 0.8, 0.9, 0.95, 0.98, 0.99));
        config.put(MAX_DEPTH, new FixedOrderRecommender(Integer.MAX_VALUE, 2, 3, 4, 5, 6, 7, 9));
        config.put(MIN_SCORE, new FixedOrderRecommender(0.00000000000001, Double.MIN_VALUE, 0.0, 0.000001, 0.0001, 0.001, 0.01, 0.1));
        config.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(5, 0, 1, 64, 1024, 4098));
        config.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 10, 100, 1000, 10000, 100000));
        config.put(SCORER, new FixedOrderRecommender(new MSEScorer(FALSE), new MSEScorer(TRUE), new SplitDiffScorer(), new InformationGainScorer(), new GiniImpurityScorer()));
        config.put(PENALIZE_CATEGORICAL_SPLITS, new FixedOrderRecommender(true, false));
        return config;
    }




}
