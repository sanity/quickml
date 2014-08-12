package quickml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.supervised.crossValidation.StationaryCrossValidator;
import quickml.supervised.crossValidation.crossValLossFunctions.ClassifierRMSECrossValLossFunction;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

/**
 * Created by ian on 7/4/14.
 */
public class PredictiveAccuracyTests {
    private static final  Logger logger =  LoggerFactory.getLogger(PredictiveAccuracyTests.class);

    @Test
    public void irisTest() throws Exception {
        StationaryCrossValidator stationaryCrossValidator = new StationaryCrossValidator(new ClassifierRMSECrossValLossFunction());
        final double crossValidatedLoss = stationaryCrossValidator.getCrossValidatedLoss(new RandomForestBuilder(), Benchmarks.loadIrisDataset());
        double previousLoss = 0.673;
        logger.info("Cross Validated Lost: {}", crossValidatedLoss);
        Assert.assertTrue(crossValidatedLoss <= previousLoss, String.format("Current loss is %s, but previous loss was %s, this is a regression", crossValidatedLoss, previousLoss));
        Assert.assertTrue(crossValidatedLoss > previousLoss * 0.95, String.format("Current loss is %s, but previous loss was %s, this is a significant improvement, previousLoss should be updated", crossValidatedLoss, previousLoss));

    }
}
