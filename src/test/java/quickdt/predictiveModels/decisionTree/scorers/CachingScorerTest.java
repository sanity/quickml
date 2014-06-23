package quickdt.predictiveModels.decisionTree.scorers;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.data.Instance;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.util.List;

/**
 * Created by chrisreeves on 6/23/14.
 */
public class CachingScorerTest {
    @Test
    public void timeTest() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(100000);
        TreeBuilder tb = new TreeBuilder(new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE));
        RandomForestBuilder rb = new RandomForestBuilder(tb);
        final long startTime = System.currentTimeMillis();
        rb.buildPredictiveModel(instances);
        final long duration = System.currentTimeMillis() - startTime;

        TreeBuilder tb2 = new TreeBuilder(new CachingScorer(new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE), 1000));
        RandomForestBuilder rb2 = new RandomForestBuilder(tb2);
        final long startTime2 = System.currentTimeMillis();
        rb2.buildPredictiveModel(instances);
        final long duration2 = System.currentTimeMillis() - startTime2;
        Assert.assertTrue(duration >= duration2, "Cached scorer should be faster");
    }
}
