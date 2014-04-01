package quickdt.experiments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.data.AbstractInstance;
import quickdt.Benchmarks;
import quickdt.experiments.crossValidation.CrossValidator;
import quickdt.predictiveModelOptimizer.PredictiveModelOptimizer;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilderBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 3/1/14.
 */
public class PredictiveModelOptimizerTest {
    private static final  Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizerTest.class);

    @Test
    public void irisDatasetTest() throws IOException {
        final List<AbstractInstance> instances = Benchmarks.loadIrisDataset();
        final RandomForestBuilderBuilder predictiveModelBuilderBuilder = new RandomForestBuilderBuilder();
        PredictiveModelOptimizer predictiveModelOptimizer = new PredictiveModelOptimizer(predictiveModelBuilderBuilder, instances);
        final Map<String, Object> optimalParameters = predictiveModelOptimizer.findOptimalParameters();
        logger.error("Optimal parameters: " + optimalParameters);
        RandomForestBuilder defaultRFBuilder = new RandomForestBuilder();
        final RandomForestBuilder optimalRFBuilder = predictiveModelBuilderBuilder.buildBuilder(optimalParameters);
        CrossValidator crossValidator = new CrossValidator();
        double defaultLoss = crossValidator.getCrossValidatedLoss(defaultRFBuilder, instances);
        double optimizedLoss = crossValidator.getCrossValidatedLoss(optimalRFBuilder, instances);
        logger.info("Default PM loss: "+defaultLoss+", optimized PM loss: "+optimizedLoss);
        Assert.assertTrue(optimizedLoss <= defaultLoss, "Default PM loss ("+defaultLoss+") should be higher or equal to optimized PM loss ("+optimizedLoss+")");
    }

}
