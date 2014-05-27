package quickdt.experiments;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.*;
import quickdt.csvReader.CSVToMap;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.data.Instance;
import quickdt.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.calibratedPredictiveModel.PAVCalibratedPredictiveModelBuilderBuilder;
import quickdt.predictiveModels.calibratedPredictiveModel.UpdatablePAVCalibratedPredictiveModelBuilderBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilderBuilder;
import quickdt.predictiveModels.downsamplingPredictiveModel.DownsamplingPredictiveModelBuilderBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilderBuilder;
import quickdt.predictiveModels.randomForest.UpdatableRandomForestBuilderBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisreeves on 5/23/14.
 */
public class PredictiveModelOptimizerRunner {

    private static final Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizer.class);


    public static void main(String[] args) throws IOException {
        List<PredictiveModelBuilderBuilder> builderBuilders = getPredictiveModelBuilderBuilders();
        Iterable<? extends AbstractInstance> trainingData = getTrainingData("/Users/chrisreeves/Downloads/redshift_training_data.csv");
        List<BidderConfiguration> configurations = Lists.newLinkedList();

        for(PredictiveModelBuilderBuilder builderBuilder : builderBuilders) {
            CrossValidator crossValidator = getCrossValidator();
            PredictiveModelOptimizer predictiveModelOptimizer = new PredictiveModelOptimizer(builderBuilder, trainingData, crossValidator);
            final Map<String, Object> optimalParameters = predictiveModelOptimizer.determineOptimalConfiguration();
            Double loss = predictiveModelOptimizer.getLoss(optimalParameters);
            configurations.add(new BidderConfiguration(builderBuilder, optimalParameters, loss));
        }

        Collections.sort(configurations, new Comparator<BidderConfiguration>() {
            @Override
            public int compare(final BidderConfiguration o1, final BidderConfiguration o2) {
                return o1.loss.compareTo(o2.loss);
            }
        });
    }

    private static CrossValidator getCrossValidator() {
        return new OutOfTimeCrossValidator(new RMSECrossValLoss(), 0.25, 30, new TrainingDateTimeExtractor());
    }

    private static List<PredictiveModelBuilderBuilder> getPredictiveModelBuilderBuilders() {
        List<PredictiveModelBuilderBuilder> builderBuilders = Lists.newLinkedList();
        builderBuilders.add(new TreeBuilderBuilder());
        //builderBuilders.add(new UpdatableRandomForestBuilderBuilder(new RandomForestBuilderBuilder()));
        //builderBuilders.add(new UpdatablePAVCalibratedPredictiveModelBuilderBuilder(new PAVCalibratedPredictiveModelBuilderBuilder()));
        //builderBuilders.add(new DownsamplingPredictiveModelBuilderBuilder(new PAVCalibratedPredictiveModelBuilderBuilder()));
        return builderBuilders;
    }

    private static Iterable<? extends AbstractInstance> getTrainingData(String filename) throws IOException {
        List<Map<String, Serializable>> instanceMaps = CSVToMap.loadRows(filename);
        logger.info("Read " + instanceMaps.size() + " from instances " + filename);
        List<Instance> instances = csvReaderExp.convertRawMapToInstance(instanceMaps);
        System.out.println("Loaded " + instances.size() + " instances");
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

    private static class TrainingDateTimeExtractor implements DateTimeExtractor {
        @Override
        public DateTime extractDateTime(AbstractInstance instance) {
            Attributes attributes = instance.getAttributes();
            int currentTimeMillis = (Integer)attributes.get("currentTimeMillis");
            return new DateTime(currentTimeMillis);
        }
    }
}
