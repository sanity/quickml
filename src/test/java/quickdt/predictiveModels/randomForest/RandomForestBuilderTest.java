package quickdt.predictiveModels.randomForest;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.data.Attributes;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModelWithDataBuilder;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 5/14/2014.
 */
public class RandomForestBuilderTest {
    @Test
    public void simpleBmiTest() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder rfb = new RandomForestBuilder(tb);
        final long startTime = System.currentTimeMillis();
        final RandomForest randomForest = rfb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final Attributes testAttributes = instances.get(0).getAttributes();
        for (Map.Entry<Serializable, Double> entry : randomForest.predict(testAttributes).entrySet()) {
            Assert.assertEquals(entry.getValue(), randomForest.getProbability(testAttributes, entry.getKey()));
        }
    }

    @Test
    public void simpleBmiTestSplit() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(10000);
        final PredictiveModelWithDataBuilder<RandomForest> wb = getWrappedUpdatablePredictiveModelBuilder();
        wb.splitNodeThreshold(1);
        final long startTime = System.currentTimeMillis();
        final RandomForest randomForest = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        int treeSize = trees.size();
        int firstTreeNodeSize = trees.get(0).node.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final RandomForest newRandomForest = wb.buildPredictiveModel(newInstances);
        Assert.assertTrue(randomForest == newRandomForest, "Expect same tree to be updated");
        Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same number of trees");
        Assert.assertNotEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected new nodes");

        treeSize = newRandomForest.trees.size();
        firstTreeNodeSize = newRandomForest.trees.get(0).node.size();
        wb.stripData(newRandomForest);
        Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same trees");
        Assert.assertEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected same nodes");
    }

    private PredictiveModelWithDataBuilder<RandomForest> getWrappedUpdatablePredictiveModelBuilder() {
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer()).updatable(true);
        final RandomForestBuilder urfb = new RandomForestBuilder(tb);
        return new PredictiveModelWithDataBuilder<>(urfb);
    }

    @Test
    public void simpleBmiTestNoSplit() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(10000);
        final PredictiveModelWithDataBuilder<RandomForest> wb = getWrappedUpdatablePredictiveModelBuilder();
        final long startTime = System.currentTimeMillis();
        final RandomForest randomForest = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        int treeSize = trees.size();
        int firstTreeNodeSize = trees.get(0).node.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final RandomForest newRandomForest = wb.buildPredictiveModel(newInstances);
        Assert.assertTrue(randomForest == newRandomForest, "Expect same tree to be updated");
        Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same number of trees");
        Assert.assertEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected same nodes");

        treeSize = newRandomForest.trees.size();
        firstTreeNodeSize = newRandomForest.trees.get(0).node.size();
        wb.stripData(newRandomForest);
        Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same trees");
        Assert.assertEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected same nodes");
    }
}
