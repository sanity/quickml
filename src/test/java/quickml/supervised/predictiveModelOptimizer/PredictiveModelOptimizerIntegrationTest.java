package quickml.supervised.predictiveModelOptimizer;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.scorers.InformationGainScorer;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;
import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.MonotonicConvergenceRecommender;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static quickml.supervised.InstanceLoader.getAdvertisingInstances;
import static quickml.supervised.classifier.decisionTree.TreeBuilder.*;
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
                .lossChecker(new ClassifierLossChecker<>(new WeightedAUCCrossValLossFunction(1.0)))
                .valuesToTest(createConfig())
                .iterations(3)
                .build();
    }


    @Test
    public void testOptimizer() throws Exception {
        System.out.println("optimalConfig = " + optimizer.determineOptimalConfig());
    }


    private Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        config.put(NUM_TREES, new FixedOrderRecommender(12, 24));
        config.put(IGNORE_ATTR_PROB, new FixedOrderRecommender(0.7, 0.5));
        config.put(MAX_DEPTH, new FixedOrderRecommender(4, 8, 16));//Integer.MAX_VALUE, 2, 3, 5, 6, 9));
        config.put(MIN_CAT_ATTR_OCC, new FixedOrderRecommender(7, 10, 15));
        config.put(MIN_LEAF_INSTANCES, new FixedOrderRecommender(0, 15, 30));
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY, new FixedOrderRecommender(1.0, 0.75, .5 ));
        return config;
    }



}
