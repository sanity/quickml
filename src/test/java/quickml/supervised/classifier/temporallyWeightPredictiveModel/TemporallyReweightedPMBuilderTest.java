package quickml.supervised.classifier.temporallyWeightPredictiveModel;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.supervised.crossValidation.dateTimeExtractors.MapDateTimeExtractor;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisreeves on 6/23/14.
 */
public class TemporallyReweightedPMBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testError3ClassificationsInDataSet() throws Exception {
        final List<Instance<Map<String,Serializable>>> instances = new LinkedList<>();
        Map<String,Serializable> map = new HashMap<>();
        map.put("2", "2");
        instances.add(new InstanceImpl<Map<String, Serializable>>(map, "1"));
        instances.add(new InstanceImpl<Map<String, Serializable>>(map, "2"));
        instances.add(new InstanceImpl<Map<String, Serializable>>(map, "3"));
        PredictiveModelBuilder predictiveModelBuilder = new TreeBuilder();
        final TemporallyReweightedPMBuilder cpmb = new TemporallyReweightedPMBuilder(predictiveModelBuilder, new MapDateTimeExtractor());
        cpmb.buildPredictiveModel(instances);
    }

    @Test
    public void simpleBmiTest() throws Exception {
        final List<Instance<Map<String,Serializable>>> instances = TreeBuilderTestUtils.getIntegerInstances(10000);
        final PredictiveModelBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final TemporallyReweightedPMBuilder builder = new TemporallyReweightedPMBuilder(tb, new MapDateTimeExtractor());
        final long startTime = System.currentTimeMillis();
        final TemporallyReweightedPM model = builder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(model);

        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");
    }

}
