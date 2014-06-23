package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.crossValidation.SampleDateTimeExtractor;
import quickdt.data.Instance;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;

import java.util.List;

/**
 * Created by chrisreeves on 6/23/14.
 */
public class TemporallyReweightedPMBuilderTest {
    @Test
    public void simpleBmiTest() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getIntegerInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final TemporallyReweightedPMBuilder builder = new TemporallyReweightedPMBuilder(tb, new SampleDateTimeExtractor());
        final long startTime = System.currentTimeMillis();
        final TemporallyReweightedPM model = builder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(model);

        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");
    }

}
