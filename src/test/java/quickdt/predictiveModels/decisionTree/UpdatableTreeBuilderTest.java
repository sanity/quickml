package quickdt.predictiveModels.decisionTree;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.data.Instance;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;

import java.util.List;


public class UpdatableTreeBuilderTest {
	@Test
	public void simpleBmiTestSplit() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(10000);
		final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final UpdatableTreeBuilder utb = new UpdatableTreeBuilder(tb, null, 1);
		final long startTime = System.currentTimeMillis();
        final Tree tree = utb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.node);

        int nodeSize = tree.node.size();
        double nodeMeanDepth = tree.node.meanDepth();
		Assert.assertTrue(nodeSize < 400, "Tree size should be less than 400 nodes");
		Assert.assertTrue(nodeMeanDepth < 6, "Mean depth should be less than 6");
		Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final Tree newTree = utb.buildPredictiveModel(newInstances);
        Assert.assertTrue(tree == newTree, "Expect same tree to be updated");
        Assert.assertNotEquals(nodeSize, newTree.node.size(), "Expected new nodes");
        Assert.assertNotEquals(nodeMeanDepth, newTree.node.meanDepth(), "Expected new mean depth");

        nodeSize = newTree.node.size();
        nodeMeanDepth = newTree.node.meanDepth();
        utb.stripData(newTree);
        Assert.assertEquals(nodeSize, newTree.node.size(), "Expected same nodes");
        Assert.assertEquals(nodeMeanDepth, newTree.node.meanDepth(), "Expected same mean depth");
    }

    @Test
    public void simpleBmiTestNoSplit() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final UpdatableTreeBuilder utb = new UpdatableTreeBuilder(tb);
        final long startTime = System.currentTimeMillis();
        final Tree tree = utb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.node);

        int nodeSize = tree.node.size();
        double nodeMeanDepth = tree.node.meanDepth();
        Assert.assertTrue(nodeSize < 400, "Tree size should be less than 400 nodes");
        Assert.assertTrue(nodeMeanDepth < 6, "Mean depth should be less than 6");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final Tree newTree = utb.buildPredictiveModel(newInstances);
        Assert.assertTrue(tree == newTree, "Expect same tree to be updated");
        Assert.assertEquals(nodeSize, newTree.node.size(), "Expected same nodes");
        Assert.assertNotEquals(nodeMeanDepth, newTree.node.meanDepth(), "Expected new mean depth");

        nodeSize = newTree.node.size();
        nodeMeanDepth = newTree.node.meanDepth();
        utb.stripData(newTree);
        Assert.assertEquals(nodeSize, newTree.node.size(), "Expected same nodes");
        Assert.assertEquals(nodeMeanDepth, newTree.node.meanDepth(), "Expected same mean depth");
    }
}
