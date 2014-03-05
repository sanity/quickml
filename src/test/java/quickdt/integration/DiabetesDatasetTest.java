package quickdt.integration;

import com.google.common.collect.Iterables;
import junit.framework.Assert;
import org.testng.annotations.Test;
import quickdt.*;
import quickdt.experiments.crossValidation.CrossValidator;
import quickdt.experiments.crossValidation.RMSECrossValLoss;
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

        System.out.println("Total instance count: "+ Iterables.size(instances));

        CrossValidator crossValidator = new CrossValidator();
        PredictiveModelBuilder<?> predictiveModelBuilder = new RandomForestBuilder(new TreeBuilder().ignoreAttributeAtNodeProbability(0.5).minimumScore(0.001)).numTrees(100);

        final double rmse = crossValidator.getCrossValidatedLoss(predictiveModelBuilder, instances);
        System.out.println("RMSE on Diabetes dataset: "+rmse);
        Assert.assertTrue(String.format("RMSE is %f, should be below 0.499", rmse), rmse < 0.48);
    }

}
