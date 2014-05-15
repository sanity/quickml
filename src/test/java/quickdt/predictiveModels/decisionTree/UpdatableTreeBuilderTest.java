package quickdt.predictiveModels.decisionTree;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;
import quickdt.Misc;
import quickdt.data.Instance;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;

import java.util.List;
import java.util.Set;


public class UpdatableTreeBuilderTest {
	@Test
	public void simpleBmiTest() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(10000);
		final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final UpdatableTreeBuilder utb = new UpdatableTreeBuilder(tb);
		final long startTime = System.currentTimeMillis();
        final Tree tree = utb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.node);

        final int nodeSize = tree.node.size();
        final double nodeMeanDepth = tree.node.meanDepth();
		Assert.assertTrue(nodeSize < 400, "Tree size should be less than 400 nodes");
		Assert.assertTrue(nodeMeanDepth < 6, "Mean depth should be less than 6");
		Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final Tree newTree = utb.buildPredictiveModel(newInstances);
        Assert.assertTrue(tree == newTree, "Expect same tree to be updated");
        Assert.assertNotEquals(nodeSize, tree.node.size(), "Expected new nodes");
        Assert.assertNotEquals(nodeMeanDepth, tree.node.meanDepth(), "Expected new mean depth");
	}



    @Test(enabled = false)
	public void multiScorerBmiTest() {
		final Set<Instance> instances = Sets.newHashSet();

		for (int x = 0; x < 10000; x++) {
			final double height = (4 * 12) + Misc.random.nextInt(3 * 12);
			final double weight = 120 + Misc.random.nextInt(110);
			final Instance instance = Instance.create(TreeBuilderTestUtils.bmiHealthy(weight, height), "weight", weight, "height", height);
			instances.add(instance);
		}
		{
			final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
			final Tree tree = tb.buildPredictiveModel(instances);
			System.out.println("SplitDiffScorer node size: " + tree.node.size());
		}
	}


}
