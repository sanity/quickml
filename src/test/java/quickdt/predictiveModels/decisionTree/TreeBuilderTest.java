package quickdt.predictiveModels.decisionTree;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;
import quickdt.Misc;
import quickdt.data.Instance;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;
import quickdt.predictiveModels.decisionTree.tree.Node;

import java.util.List;
import java.util.Set;


public class TreeBuilderTest {
	@Test
	public void simpleBmiTest() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(10000);
		final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
		final long startTime = System.currentTimeMillis();
        final Tree tree = tb.buildPredictiveModel(instances);
		final Node node = tree.node;

        TreeBuilderTestUtils.serializeDeserialize(node);

        final int nodeSize = node.size();
        final double nodeMeanDepth = node.meanDepth();
		Assert.assertTrue(nodeSize < 400, "Tree size should be less than 400 nodes");
		Assert.assertTrue(nodeMeanDepth < 6, "Mean depth should be less than 6");
		Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");
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
