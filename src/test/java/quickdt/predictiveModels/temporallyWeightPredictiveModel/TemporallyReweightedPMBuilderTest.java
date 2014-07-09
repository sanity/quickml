package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.crossValidation.SampleDateTimeExtractor;
import quickdt.data.Instance;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chrisreeves on 6/23/14.
 */
public class TemporallyReweightedPMBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testError3ClassificationsInDataSet() throws Exception {
        final List<Instance> instances = new LinkedList<>();
        instances.add(Instance.create("1", "2", "2"));
        instances.add(Instance.create("2", "2", "2"));
        instances.add(Instance.create("3", "2", "2"));
        final TemporallyReweightedPMBuilder cpmb = new TemporallyReweightedPMBuilder(new TreeBuilder(), new SampleDateTimeExtractor());
        cpmb.buildPredictiveModel(instances);
    }

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
