package quickml.supervised.classifier.temporallyWeightClassifier;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.crossValidation.dateTimeExtractors.MapDateTimeExtractor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chrisreeves on 6/23/14.
 */
public class TemporallyReweightedClassifierBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testError3ClassificationsInDataSet() throws Exception {
        final List<Instance<AttributesMap, Serializable>> instances = new LinkedList<>();
        AttributesMap  map = AttributesMap.newHashMap() ;
        map.put("2", "2");
        instances.add(new InstanceImpl<AttributesMap, Serializable>(map, "1"));
        instances.add(new InstanceImpl<AttributesMap, Serializable>(map, "2"));
        instances.add(new InstanceImpl<AttributesMap, Serializable>(map, "3"));
        PredictiveModelBuilder predictiveModelBuilder = new TreeBuilder();
        final TemporallyReweightedClassifierBuilder cpmb = new TemporallyReweightedClassifierBuilder(predictiveModelBuilder, new MapDateTimeExtractor());
        cpmb.buildPredictiveModel(instances);
    }

    @Test
    public void simpleBmiTest() throws Exception {
        final List<Instance<AttributesMap, Serializable>> instances = TreeBuilderTestUtils.getIntegerInstances(10000);
        final PredictiveModelBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final TemporallyReweightedClassifierBuilder builder = new TemporallyReweightedClassifierBuilder(tb, new MapDateTimeExtractor());
        final long startTime = System.currentTimeMillis();
        final TemporallyReweightedClassifier model = builder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(model);

        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");
    }

}
