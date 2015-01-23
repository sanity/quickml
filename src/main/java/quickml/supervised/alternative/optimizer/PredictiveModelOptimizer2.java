package quickml.supervised.alternative.optimizer;

import com.google.common.collect.Maps;
import quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;

import java.util.*;

public class PredictiveModelOptimizer2 {

    public static final int MAX_ITERATIONS = 10;

    private Map<String, FixedOrderRecommender> fieldRecommenders;
    private ModelTester modelTester;
    private HashMap<String, Object> bestConfig;

    public PredictiveModelOptimizer2(Map<String, FixedOrderRecommender> config, ModelTester modelTester) {
        this.fieldRecommenders = config;
        this.modelTester = modelTester;
        this.bestConfig = setBestConfigToFirstValues(config);
    }

    /**
     * We find the value for each field that results in the lowest loss
     * Then repeat the process starting with the optimized configuration
     * Keep going until we are no longer improving or we have reached max_iterations
     */
    public Map<String, Object> determineOptimalConfig() {
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            HashMap<String, Object> previousConfig = copyOf(bestConfig);
            updateBestConfig();
            if (bestConfig.equals(previousConfig))
                break;
        }
        return bestConfig;
    }

    private void updateBestConfig() {
        for (String field : fieldRecommenders.keySet()) {
            findBestValueForField(field);
        }
    }

    private void findBestValueForField(String field) {
        FieldLosses losses = new FieldLosses();

        for (Object value : fieldRecommenders.get(field).getValues()) {
            bestConfig.put(field, value);
            losses.add(new FieldLoss(value, modelTester.testModel(bestConfig)));
        }

        bestConfig.put(field, losses.valueWithLowestLoss());
    }

    private HashMap<String, Object> setBestConfigToFirstValues(Map<String, FixedOrderRecommender> config) {
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, FixedOrderRecommender> entry : config.entrySet()) {
            map.put(entry.getKey(), entry.getValue().first());
        }
        return map;
    }

    private HashMap<String, Object> copyOf(final HashMap<String, Object> map) {
        return Maps.newHashMap(map);
    }


    /**
     * Convience class to sort and return the value with the lowest loss
     */
    private static class FieldLosses {
        private List<FieldLoss> losses = new ArrayList<>();

        public void add(FieldLoss fieldLoss) {
            losses.add(fieldLoss);
        }

        public Object valueWithLowestLoss() {
            Collections.sort(losses);
            return losses.get(0).fieldValue;
        }
    }

    private static class FieldLoss implements Comparable<FieldLoss> {
        private final Object fieldValue;
        private final double loss;

        public FieldLoss(Object fieldValue, double loss) {
            this.fieldValue = fieldValue;
            this.loss = loss;
        }

        @Override
        public int compareTo(FieldLoss o) {
            return Double.compare(this.loss, o.loss);
        }
    }


}
