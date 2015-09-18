package quickml.supervised.predictiveModelOptimizer;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;

import static quickml.supervised.tree.constants.ForestOptions.*;

import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.tree.attributeIgnoringStrategies.CompositeAttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesInSet;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.MonotonicConvergenceRecommender;
import quickml.supervised.tree.decisionTree.scorers.GRPenalizedGiniImpurityScorerFactory;
import quickml.supervised.tree.decisionTree.scorers.PenalizedInformationGainScorerFactory;

import java.util.*;

import static java.util.Arrays.asList;
import static quickml.InstanceLoader.getAdvertisingInstances;

public class PredictiveModelOptimizerIntegrationTest {


    private PredictiveModelOptimizer optimizer;

    @Before
    public void setUp() throws Exception {
        List<ClassifierInstance> advertisingInstances = getAdvertisingInstances();
        advertisingInstances = advertisingInstances.subList(0, 3000);
        optimizer = new PredictiveModelOptimizerBuilder<RandomDecisionForest, ClassifierInstance>()
                .modelBuilder(new RandomDecisionForestBuilder<>())
                .dataCycler(new OutOfTimeData<>(advertisingInstances, 0.2, 12, new OnespotDateTimeExtractor()))
                .lossChecker(new ClassifierLossChecker<ClassifierInstance, RandomDecisionForest>(new WeightedAUCCrossValLossFunction(1.0)))
                .valuesToTest(createConfig())
                .iterations(2)
                .build();
    }


    @Test
    public void testOptimizer() throws Exception {
        System.out.println("optimalConfig = " + optimizer.determineOptimalConfig());
    }
    
    private Map<String, FieldValueRecommender> createConfig() {
        Map<String, FieldValueRecommender> config = Maps.newHashMap();
        Set<String> attributesToIgnore = Sets.newHashSet();
        attributesToIgnore.addAll(Arrays.asList("browser", "eap", "destinationId", "seenPixel", "internalCreativeId"));
        double probabilityOfDiscardingFromAttributesToIgnore = 0.3;
        CompositeAttributeIgnoringStrategy compositeAttributeIgnoringStrategy = new CompositeAttributeIgnoringStrategy(Arrays.asList(
                new IgnoreAttributesWithConstantProbability(0.7), new IgnoreAttributesInSet(attributesToIgnore, probabilityOfDiscardingFromAttributesToIgnore)
        ));
        config.put(ATTRIBUTE_IGNORING_STRATEGY.name(), new FixedOrderRecommender(new IgnoreAttributesWithConstantProbability(0.7), compositeAttributeIgnoringStrategy ));
        config.put(NUM_TREES.name(), new MonotonicConvergenceRecommender(asList(20)));
        config.put(MAX_DEPTH.name(), new FixedOrderRecommender( 4, 8, 16));//Integer.MAX_VALUE, 2, 3, 5, 6, 9));
        config.put(MIN_SCORE.name(), new FixedOrderRecommender(0.00000000000001));//, Double.MIN_VALUE, 0.0, 0.000001, 0.0001, 0.001, 0.01, 0.1));
        config.put(ATTRIBUTE_VALUE_THRESHOLD_OBSERVATIONS.name(), new FixedOrderRecommender(2, 11, 16, 30 ));
        config.put(MIN_LEAF_INSTANCES.name(), new FixedOrderRecommender(0, 20, 40));
        config.put(SCORER_FACTORY.name(), new FixedOrderRecommender(new PenalizedInformationGainScorerFactory(), new GRPenalizedGiniImpurityScorerFactory()));
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY.name(), new FixedOrderRecommender(1.0, 0.75, .5 ));
        return config;
    }



}
