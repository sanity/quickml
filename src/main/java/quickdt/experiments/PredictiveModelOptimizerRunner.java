package quickdt.experiments;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.CrossValidator;
import quickdt.crossValidation.DateTimeExtractor;
import quickdt.crossValidation.NonWeightedAUCCrossValLoss;
import quickdt.crossValidation.OutOfTimeCrossValidator;
import quickdt.csvReader.CSVToMap;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.data.Instance;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.FixedOrderRecommender;
import quickdt.predictiveModelOptimizer.fieldValueRecommenders.MonotonicConvergenceRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.PredictiveModelWithDataBuilderBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilderBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chrisreeves on 5/23/14.
 */
public class PredictiveModelOptimizerRunner {

    private static final Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizerRunner.class);


    public static void main(String[] args) throws IOException {
        List<PredictiveModelBuilderBuilder> builderBuilders = getPredictiveModelBuilderBuilders();
        Iterable<? extends AbstractInstance> trainingData = getTrainingData("redshift_training_data.csv");
        List<BidderConfiguration> configurations = Lists.newLinkedList();

        for(PredictiveModelBuilderBuilder builderBuilder : builderBuilders) {
            CrossValidator crossValidator = getCrossValidator();
            Map<String, FieldValueRecommender> parametersToOptimize = getParametersToOptimize();
            PredictiveModelOptimizer predictiveModelOptimizer = new PredictiveModelOptimizer(builderBuilder, trainingData, crossValidator,parametersToOptimize);
            Map<String, Object> initialConfiguration = getInitialConfiguration();
            final Map<String, Object> optimalParameters = predictiveModelOptimizer.determineOptimalConfiguration(initialConfiguration);
            Double loss = new Double(0);//predictiveModelOptimizer.getLoss(optimalParameters);
            configurations.add(new BidderConfiguration(builderBuilder, optimalParameters, loss));
        }

        Collections.sort(configurations, new Comparator<BidderConfiguration>() {
            @Override
            public int compare(final BidderConfiguration o1, final BidderConfiguration o2) {
                return o1.loss.compareTo(o2.loss);
            }
        });

        logger.info(configurations.get(0).builderBuilder.toString() + " loss:" + configurations.get(0).loss + " " + configurations.get(0).optimalParameters);
        logger.info(configurations.get(configurations.size() - 1).builderBuilder.toString() + " loss:" + configurations.get(configurations.size()-1).loss + " " + configurations.get(configurations.size()-1).optimalParameters);
    }

    private static Map<String, FieldValueRecommender> getParametersToOptimize(){
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.put("minLeafInstances", new FixedOrderRecommender(40, 60, 70));
        parametersToOptimize.put("ignoreAttrProb", new FixedOrderRecommender(0.6, 0.7, 0.8));// 0.95, 0.98, 0.99));
        parametersToOptimize.put("numTrees", new MonotonicConvergenceRecommender(Lists.newArrayList(30, 50)));

        return parametersToOptimize;
    }

    private static Map<String, Object> getInitialConfiguration(){
        Map<String, Object> initialConfiguration = Maps.newHashMap();
        initialConfiguration.put("minLeafInstances", new Integer(30));
        initialConfiguration.put("ignoreAttrProb", new Double(.7));
        initialConfiguration.put("maxDepth", new Integer(20));
        initialConfiguration.put("minScore", new Double(0));
        initialConfiguration.put("minCatAttrOcc",  new Integer(0));
        initialConfiguration.put("bagSize", new Integer(0));
        initialConfiguration.put("numTrees", new Integer(30));
        initialConfiguration.put("executorThreadCount", new Integer(8));
        initialConfiguration.put("rebuildThreshold", new Integer(12));
        initialConfiguration.put("splitThreshold", new Integer(2));

        return initialConfiguration;
    }


    private static CrossValidator getCrossValidator() {
        return new OutOfTimeCrossValidator(new NonWeightedAUCCrossValLoss(), 0.15, 30, new TrainingDateTimeExtractor());
    }

    private static List<PredictiveModelBuilderBuilder> getPredictiveModelBuilderBuilders() {
        List<PredictiveModelBuilderBuilder> builderBuilders = Lists.newLinkedList();
        builderBuilders.add(new PredictiveModelWithDataBuilderBuilder(new RandomForestBuilderBuilder()));
        return builderBuilders;
    }

    private static Iterable<? extends AbstractInstance> getTrainingData(String filename) throws IOException {
        List<Map<String, Serializable>> instanceMaps = CSVToMap.loadRows(filename);
        List<Instance> instances = CsvReaderExp.convertRawMapToInstance(instanceMaps);
        logger.info("Read " + instances.size() + " instances");
        return instances;
    }

    static class BidderConfiguration {
        private Map<String, Object> optimalParameters;
        private PredictiveModelBuilderBuilder builderBuilder;
        private Double loss;

        public BidderConfiguration(PredictiveModelBuilderBuilder builderBuilder, Map<String, Object> optimalParameters, Double loss) {
            this.builderBuilder = builderBuilder;
            this.optimalParameters = optimalParameters;
            this.loss = loss;
        }
    }

    public static class TrainingDateTimeExtractor implements DateTimeExtractor {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @Override
        public DateTime extractDateTime(AbstractInstance instance) {
            Attributes attributes = instance.getAttributes();
            try {
                Date currentTimeMillis = dateFormat.parse((String)attributes.get("created_at"));
                return new DateTime(currentTimeMillis);
            } catch (ParseException e) {
                logger.error("Error parsing date", e);
            }
            return new DateTime();
        }
    }
}
