package quickml.supervised.predictiveModelOptimizer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.crossValidation.CrossValidator;

import java.io.Serializable;
import java.util.*;

public class PredictiveModelOptimizer {

    private static final Logger logger = LoggerFactory.getLogger(PredictiveModelOptimizer.class);

    private Map<String, ? extends FieldValueRecommender> fieldsToOptimize; //should pass in
    private final CrossValidator crossValidator;
    private HashMap<String, Serializable> localBestConfig;  //should be a param: not be stateful
    private HashMap<String, Serializable> bestConfig;  //should be a param: not be stateful
    private final int iterations;//should be param
    private int iteration; //should not be a field
    private List<ConfigWithLoss> configsWithLosses = Lists.newArrayList();


    /**
     * Do not call directly - Use PredictiveModelOptimizerBuilder to an instance
     * @param fieldsToOptimize - key is the field - e.g. maxDepth, FixedOrderRecommender is a set of values for maxDepth to try
     * @param crossValidator - Model tester takes a configuration and returns the loss
     */

    public PredictiveModelOptimizer(Map<String, ? extends FieldValueRecommender> fieldsToOptimize, CrossValidator crossValidator, int iterations) {
        this.fieldsToOptimize = fieldsToOptimize;
        this.crossValidator = crossValidator;
        this.iterations = iterations;
        this.localBestConfig = setBestConfigToFirstValues(fieldsToOptimize);
    }


    /**
     * We find the value for each field that results in the lowest loss
     * Then repeat the process starting with the optimized configuration
     * Keep going until we are no longer improving or we have reached max_iterations
     */
    public Map<String, Serializable> determineOptimalConfig() {
        for (iteration = 0; iteration < iterations; iteration++) {
            logger.info("Starting iteration - {}", iteration);
            HashMap<String, Serializable> previousConfig = copyOf(localBestConfig);
            updateBestConfig();
            if (localBestConfig.equals(previousConfig))
                break;
        }
        sortConfigsWithLosses();
        return configsWithLosses.get(0).config;
    }

    public List<ConfigWithLoss> exploreConfigs() {
        configsWithLosses = Lists.newArrayList();
        for (iteration = 0; iteration < iterations; iteration++) {
            logger.info("Starting iteration - {}", iteration);
            HashMap<String, Serializable> previousConfig = copyOf(localBestConfig);
            updateBestConfig();
            if (localBestConfig.equals(previousConfig))
                break;
        }
        return configsWithLosses;
    }

    private void sortConfigsWithLosses() {
        Collections.sort(configsWithLosses, new Comparator<ConfigWithLoss>() {
            @Override
            public int compare(ConfigWithLoss o1, ConfigWithLoss o2) {
                return Double.compare(o1.loss, o2.loss);
            }
        });
    }

    private void updateBestConfig() {
        for (String field : fieldsToOptimize.keySet()) {
            logger.info("optimizing {}", field);
            findBestValueForField(field);
        }
    }

    private void findBestValueForField(String field) {
        FieldLosses losses = new FieldLosses();
        FieldValueRecommender fieldValueRecommender = fieldsToOptimize.get(field);
        if (fieldValueRecommender.getValues().size() == 1) {
            return;
        }
        //localBestConfig is not actually localBestConfig inth for loop
        logger.info("values to try: {} ", fieldValueRecommender.getValues().toString());
        for (Serializable value : fieldValueRecommender.getValues()) {
            //TODO: make so it does not repeat a conf already seen in present iteration (e.g. keep a set of configs)
            if (localBestConfig.get(field).equals(value) && iteration > 0) {
                logger.info("skipping field value {} bc value {} already tried ", field, value );
                continue;  //safe to continue bc everything else about the config is the same.
            }
            localBestConfig.put(field, value);
            double lossForModel = crossValidator.getLossForModel(localBestConfig);
            logger.info("loss: {}, for field {}, config {}", lossForModel, field, localBestConfig);
            losses.addFieldLoss(value, lossForModel);
            if (configsWithLosses!=null) {
                configsWithLosses.add(new ConfigWithLoss(lossForModel, copyOf(localBestConfig)));
            }

            if (!fieldValueRecommender.shouldContinue(losses.getLosses()))
                break;
        }

        localBestConfig.put(field, losses.valueWithLowestLoss());
    }

    private HashMap<String, Serializable> setBestConfigToFirstValues(Map<String, ? extends FieldValueRecommender> config) {
        HashMap<String, Serializable> map = new HashMap<>();
        for (Map.Entry<String, ? extends FieldValueRecommender> entry : config.entrySet()) {
            map.put(entry.getKey(), entry.getValue().first());
        }
        logger.info("Initial Configuration - {}", map);
        return map;
    }

    private HashMap<String, Serializable> copyOf(final HashMap<String, Serializable> map) {
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

        public Serializable valueWithLowestLoss() {
            Collections.sort(losses);
            return losses.get(0).fieldValue;
        }

        public void addFieldLoss(Serializable fieldValue, double loss) {
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
        private final Serializable fieldValue;
        private final double loss;

        public FieldLoss(Serializable fieldValue, double loss) {
            this.fieldValue = fieldValue;
            this.loss = loss;
        }

        @Override
        public int compareTo(FieldLoss o) {
            return Double.compare(this.loss, o.loss);
        }
    }


}
