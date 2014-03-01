package quickdt.integration;

import junit.framework.Assert;
import org.testng.annotations.Test;
import quickdt.Benchmarks;
import quickdt.Instance;
import quickdt.experiments.crossValidation.*;
import quickdt.randomForest.RandomForestBuilder;

import java.io.IOException;
import java.util.List;

/**
 * Created by ian on 3/1/14.
 */
public class DiabetesDatasetTest {
    @Test
    public void testDiabetesDataset() throws IOException {
        final List<Instance> instances = Benchmarks.loadDiabetesDataset();
        CrossValidator crossValidator = new CrossValidator();
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder().numTrees(100);
        final RMSECrossValScorer testResult = (RMSECrossValScorer) crossValidator.test(randomForestBuilder, instances);
        Assert.assertTrue(String.format("RMSE is %f, should be below 0.447", testResult.getRMSE()), testResult.getRMSE() < 0.447);
    }
}
