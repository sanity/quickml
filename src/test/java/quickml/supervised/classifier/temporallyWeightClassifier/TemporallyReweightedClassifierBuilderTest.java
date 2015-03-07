package quickml.supervised.classifier.temporallyWeightClassifier;

import org.junit.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chrisreeves on 6/23/14.
 */
public class TemporallyReweightedClassifierBuilderTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testError3ClassificationsInDataSet() throws Exception {
        final List<ClassifierInstance> instances = new LinkedList<>();
        AttributesMap map = AttributesMap.newHashMap();
        map.put("2", "2");
        instances.add(new ClassifierInstance(map, "1"));
        instances.add(new ClassifierInstance(map, "2"));
        instances.add(new ClassifierInstance(map, "3"));
        PredictiveModelBuilder predictiveModelBuilder = new TreeBuilder();
        final TemporallyReweightedClassifierBuilder cpmb = new TemporallyReweightedClassifierBuilder(predictiveModelBuilder, 1.0, new OnespotDateTimeExtractor());
        cpmb.buildPredictiveModel(instances);
    }

    @Ignore("Reweighting implementation is broken currently")
    @Test
    public void simpleBmiTest() throws Exception {
        final List<ClassifierInstance> instances = TreeBuilderTestUtils.getIntegerInstances(10000);
        final PredictiveModelBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final TemporallyReweightedClassifierBuilder builder = new TemporallyReweightedClassifierBuilder(tb, 1.0, new OnespotDateTimeExtractor());
        final long startTime = System.currentTimeMillis();
        final TemporallyReweightedClassifier model = builder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(model);

        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000, "Building this root should take far less than 20 seconds");
    }

}
