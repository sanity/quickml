package quickml.supervised.alternative.optimizer;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PredictiveModelOptimizer2Test {

    @Mock
    ModelTester mockModelTester;

    private PredictiveModelOptimizer2 modelOptimizer;
    private HashMap<String, Object> bestConfig = Maps.newHashMap();
    private HashMap<String, Object> secondBestConfig = Maps.newHashMap();
    private HashMap<String, Object> thirdBestConfig = Maps.newHashMap();

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        // Use a tree map for deteminisic order
        Map<String, FixedOrderRecommender> fields = new TreeMap<>();
        fields.put("treeDepth", new FixedOrderRecommender(1, 2, 3, 4, 5));
        fields.put("penalize_splits", new FixedOrderRecommender(true, false));
        fields.put("scorer", new FixedOrderRecommender("A", "B", "C"));

        modelOptimizer = new PredictiveModelOptimizer2(fields, mockModelTester);
    }

    @Test
    public void testFindSimpleBestConfig() throws Exception {
        // Fields are checked in the following order - penalize_splits, scorer, treeDepth
        thirdBestConfig = createMap(1, false, "A");
        secondBestConfig = createMap(1, false, "C");
        bestConfig = createMap(5, false, "C");


        when(mockModelTester.getLossForModel(anyMap())).thenReturn(0.5);
        when(mockModelTester.getLossForModel(eq(thirdBestConfig))).thenReturn(0.4);
        when(mockModelTester.getLossForModel(eq(secondBestConfig))).thenReturn(0.2);
        when(mockModelTester.getLossForModel(eq(bestConfig))).thenReturn(0.1);

        assertEquals(bestConfig, modelOptimizer.determineOptimalConfig());
    }

    private HashMap<String, Object> createMap(int treeDepth, boolean penalizeSplits, String scorer) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("treeDepth", treeDepth);
        map.put("penalize_splits", penalizeSplits);
        map.put("scorer", scorer);
        return map;
    }
}