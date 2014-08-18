package quickml.supervised.predictiveModelOptimizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.Benchmarks;
import quickml.supervised.PredictiveModelWithDataBuilderFactory;
import quickml.supervised.classifier.randomForest.RandomForestBuilderFactory;
import quickml.supervised.crossValidation.crossValLossFunctions.ClassifierLogCVLossFunction;
import quickml.supervised.crossValidation.StationaryCrossValidator;
import quickml.data.Instance;
import quickml.supervised.PredictiveModelWithDataBuilder;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 3/1/14.
 */
public class PredictiveModelOptimizerTest {
    private static final  Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizerTest.class);

    @Test
    public void irisTest() throws IOException {
        final List<Instance<Map<String, Serializable>>> instances = Benchmarks.loadIrisDataset();
        testWithTrainingSet(instances);
    }

    @Test(enabled = false)
    public void diabetesTest() throws IOException {
        final List<Instance<Map<String, Serializable>>> instances = Benchmarks.loadDiabetesDataset();
        testWithTrainingSet(instances);
    }

    private void testWithTrainingSet(final List<Instance<Map<String, Serializable>>> instances) {
        final PredictiveModelWithDataBuilderFactory predictiveModelBuilderBuilder = new PredictiveModelWithDataBuilderFactory(new RandomForestBuilderFactory());
        final StationaryCrossValidator crossVal = new StationaryCrossValidator(4, 4, new ClassifierLogCVLossFunction());
        PredictiveModelOptimizer predictiveModelOptimizer = new PredictiveModelOptimizer(predictiveModelBuilderBuilder, instances, crossVal);
        final Map<String, Object> optimalParameters = predictiveModelOptimizer.determineOptimalConfiguration();
        logger.info("Optimal parameters: " + optimalParameters);
        RandomForestBuilder defaultRFBuilder = new RandomForestBuilder();
        final PredictiveModelWithDataBuilder optimalRFBuilder = predictiveModelBuilderBuilder.buildBuilder(optimalParameters);
        double defaultLoss = crossVal.getCrossValidatedLoss(defaultRFBuilder, instances);
        double optimizedLoss = crossVal.getCrossValidatedLoss(optimalRFBuilder, instances);
        logger.info("Default PM loss: "+defaultLoss+", optimized PM loss: "+optimizedLoss);
        Assert.assertTrue(optimizedLoss <= defaultLoss, "Default PM loss (" + defaultLoss + ") should be higher or equal to optimized PM loss (" + optimizedLoss + ")");
    }

}
