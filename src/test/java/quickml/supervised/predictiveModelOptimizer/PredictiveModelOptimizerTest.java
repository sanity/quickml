package quickml.supervised.predictiveModelOptimizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.Benchmarks;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.classifier.randomForest.RandomForestBuilderFactory;
import quickml.supervised.crossValidation.ClassifierStationaryCrossValidator;
import quickml.supervised.crossValidation.crossValLossFunctions.ClassifierLogCVLossFunction;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 3/1/14.
 */
public class PredictiveModelOptimizerTest {
    private static final  Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizerTest.class);

    @Test
    public void irisTest() throws IOException {
        final List<Instance<AttributesMap>> instances = Benchmarks.loadIrisDataset();
        testWithTrainingSet(instances);
    }

    @Test(enabled = false)
    public void diabetesTest() throws IOException {
        final List<Instance<AttributesMap>> instances = Benchmarks.loadDiabetesDataset();
        testWithTrainingSet(instances);
    }

    //TODO[mk] this test will need to be updated
    private void testWithTrainingSet(final List<Instance<AttributesMap>> instances) {
        RandomForestBuilderFactory randomForestBuilderFactory = new RandomForestBuilderFactory();
        final ClassifierStationaryCrossValidator crossVal = new ClassifierStationaryCrossValidator(4, 4, new ClassifierLogCVLossFunction());
        PredictiveModelOptimizer predictiveModelOptimizer = new PredictiveModelOptimizer(randomForestBuilderFactory, instances, crossVal);
        final Map<String, Object> optimalParameters = predictiveModelOptimizer.determineOptimalConfiguration();
        logger.info("Optimal parameters: " + optimalParameters);
        RandomForestBuilder defaultRFBuilder = new RandomForestBuilder();
        RandomForestBuilder optimalRFBuilder = randomForestBuilderFactory.buildBuilder(optimalParameters);
        double defaultLoss = crossVal.getCrossValidatedLoss(defaultRFBuilder, instances);
        double optimizedLoss = crossVal.getCrossValidatedLoss(optimalRFBuilder, instances);
        logger.info("Default PM loss: "+defaultLoss+", optimized PM loss: "+optimizedLoss);
        Assert.assertTrue(optimizedLoss <= defaultLoss, "Default PM loss (" + defaultLoss + ") should be higher or equal to optimized PM loss (" + optimizedLoss + ")");
    }

}
