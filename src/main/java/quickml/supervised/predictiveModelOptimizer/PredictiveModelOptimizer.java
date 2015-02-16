package quickml.supervised.predictiveModelOptimizer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.crossValidation.CrossValidator;

import java.util.*;

public class PredictiveModelOptimizer {

    private static final Logger logger = LoggerFactory.getLogger(PredictiveModelOptimizer.class);



    private Map<String, ? extends FieldValueRecommender> valuesToTest;
    private CrossValidator crossValidator;
    private HashMap<String, Object> bestConfig;
    private final int iterations;



    /**
     * @param valuesToTest - key is the field - e.g. maxDepth, FixedOrderRecommender is a set of values for maxDepth to try
     * @param crossValidator - Model tester takes a configuration and returns the loss
     */
    public PredictiveModelOptimizer(Map<String, ? extends FieldValueRecommender> valuesToTest, CrossValidator crossValidator, int iterations) {
        this.valuesToTest = valuesToTest;
        this.crossValidator = crossValidator;
        this.iterations = iterations;
        this.bestConfig = setBestConfigToFirstValues(valuesToTest);
    }

    /**
     * We find the value for each field that results in the lowest loss
     * Then repeat the process starting with the optimized configuration
     * Keep going until we are no longer improving or we have reached max_iterations
     */
    public Map<String, Object> determineOptimalConfig() {
        for (int i = 0; i < iterations; i++) {
            logger.info("Starting iteration - {}", i);
            HashMap<String, Object> previousConfig = copyOf(bestConfig);
            updateBestConfig();
            if (bestConfig.equals(previousConfig))
                break;
        }
        return bestConfig;
    }

    private void updateBestConfig() {
        for (String field : valuesToTest.keySet()) {
            findBestValueForField(field);
        }
    }

    private void findBestValueForField(String field) {
        FieldLosses losses = new FieldLosses();


        FieldValueRecommender fieldValueRecommender = valuesToTest.get(field);

        for (Object value : fieldValueRecommender.getValues()) {
            bestConfig.put(field, value);
            losses.addFieldLoss(value, crossValidator.getLossForModel(bestConfig));
            if (fieldValueRecommender.shouldContinue(losses.getLosses()))
                break;
        }

        bestConfig.put(field, losses.valueWithLowestLoss());
    }

    private HashMap<String, Object> setBestConfigToFirstValues(Map<String, ? extends FieldValueRecommender> config) {
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, ? extends FieldValueRecommender> entry : config.entrySet()) {
            map.put(entry.getKey(), entry.getValue().first());
        }
        logger.info("Initial Configuration - {}", map);
        return map;
    }

    private HashMap<String, Object> copyOf(final HashMap<String, Object> map) {
        return Maps.newHashMap(map);
    }


    /**
     * Convience classes to sort and return the value with the lowest loss
     */
    public static class FieldLosses {
        private List<FieldLoss> losses = new ArrayList<>();

        public void add(FieldLoss fieldLoss) {
            losses.add(fieldLoss);
        }

        public Object valueWithLowestLoss() {
            Collections.sort(losses);
            return losses.get(0).fieldValue;
        }

        public void addFieldLoss(Object fieldValue, double loss) {
            add(new FieldLoss(fieldValue, loss));
        }

        public List<Double> getLosses() {
            List<Double> rawLosses = Lists.newArrayList();
            for (FieldLoss loss : losses) {
                rawLosses.add(loss.loss);
            }
            return rawLosses;
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
