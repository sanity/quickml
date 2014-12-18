package quickml.supervised.classifier.decisionTree;

import com.beust.jcommander.internal.Lists;
import org.junit.Ignore;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.internal.annotations.Sets;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModelWithDataBuilder;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.decisionTree.tree.Node;

import java.io.Serializable;
import java.util.HashMap;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TreeBuilderTest {
	@Test
	public void simpleBmiTest() throws Exception {
        final List<Instance<AttributesMap>> instances = TreeBuilderTestUtils.getInstances(10000);
		final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
		final long startTime = System.currentTimeMillis();
        final Tree tree = tb.buildPredictiveModel(instances);
		final Node node = tree.node;

        TreeBuilderTestUtils.serializeDeserialize(node);

        final int nodeSize = node.size();
		Assert.assertTrue(nodeSize < 400, "Tree size should be less than 400 nodes");
		Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");
	}



    @Test(enabled = false)
	public void multiScorerBmiTest() {
		final Set<Instance<AttributesMap>> instances = Sets.newHashSet();

		for (int x = 0; x < 10000; x++) {
			final double height = (4 * 12) + MapUtils.random.nextInt(3 * 12);
			final double weight = 120 + MapUtils.random.nextInt(110);
            AttributesMap  attributes = AttributesMap.newHashMap() ;
            attributes.put("weight", weight);
            attributes.put("height", height);
			final Instance<AttributesMap> instance = new InstanceImpl<>(attributes, TreeBuilderTestUtils.bmiHealthy(weight, height));
			instances.add(instance);
		}
		{
			final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
			final Tree tree = tb.buildPredictiveModel(instances);
			System.out.println("SplitDiffScorer node size: " + tree.node.size());
		}
	}
    @Test
    public void treeMadeExpectedSplits() {
        final List<Instance<AttributesMap>> instances = Lists.newArrayList();
        //TODO: make code that generates test instances like these automatic
        AttributesMap attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", true);
        instances.add(new InstanceImpl<AttributesMap>(attributesMap, 1.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", true);
        instances.add(new InstanceImpl<AttributesMap>(attributesMap, 0.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", false);
        instances.add(new InstanceImpl<AttributesMap>(attributesMap, 0.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", false);
        instances.add(new InstanceImpl<AttributesMap>(attributesMap, 0.0));

        TreeBuilder treeBuilder = new TreeBuilder().minCategoricalAttributeValueOccurances(0).minLeafInstances(0);
        Tree tree = treeBuilder.buildPredictiveModel(instances);
        AttributesMap attributes = new AttributesMap();
        attributes.put("a", true);
        Assert.assertEquals(tree.getProbability(attributes, 1.0),.5);
        attributes.put("a", false);
        Assert.assertEquals(tree.getProbability(attributes, 0.0), 1.0);

    }

    //TODO: fails randomly.  fix it.
    @Ignore
    @Test
    public void simpleBmiTestSplit() throws Exception {
        final List<Instance<AttributesMap>> instances = TreeBuilderTestUtils.getInstances(10000);
        final PredictiveModelWithDataBuilder<AttributesMap ,Tree> wb = getWrappedUpdatablePredictiveModelBuilder();
        wb.splitNodeThreshold(1);
        final long startTime = System.currentTimeMillis();
        final Tree tree = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.node);

        int nodeSize = tree.node.size();
        double nodeMeanDepth = tree.node.meanDepth();
        Assert.assertTrue(nodeSize < 400, "Tree size should be less than 400 nodes");
        Assert.assertTrue(nodeMeanDepth < 6, "Mean depth should be less than 6");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance<AttributesMap>> newInstances = TreeBuilderTestUtils.getInstances(1000);
        final Tree newTree = wb.buildPredictiveModel(newInstances);
        Assert.assertTrue(tree == newTree, "Expect same tree to be updated");
        Assert.assertNotEquals(nodeSize, newTree.node.size(), "Expected new nodes");
        Assert.assertNotEquals(nodeMeanDepth, newTree.node.meanDepth(), "Expected new mean depth");

        nodeSize = newTree.node.size();
        nodeMeanDepth = newTree.node.meanDepth();
        wb.stripData(newTree);
        Assert.assertEquals(nodeSize, newTree.node.size(), "Expected same nodes");
        Assert.assertEquals(nodeMeanDepth, newTree.node.meanDepth(), "Expected same mean depth");
    }

    @Test
    public void simpleBmiTestNoSplit() throws Exception {
        final List<Instance<AttributesMap>> instances = TreeBuilderTestUtils.getInstances(10000);
        final PredictiveModelWithDataBuilder<AttributesMap ,Tree> wb = getWrappedUpdatablePredictiveModelBuilder();
        final long startTime = System.currentTimeMillis();
        final Tree tree = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.node);

        int nodeSize = tree.node.size();
        double nodeMeanDepth = tree.node.meanDepth();
        Assert.assertTrue(nodeSize < 400, "Tree size should be less than 400 nodes");
        Assert.assertTrue(nodeMeanDepth < 6, "Mean depth should be less than 6");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance<AttributesMap>> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final Tree newTree = wb.buildPredictiveModel(newInstances);
        Assert.assertTrue(tree == newTree, "Expect same tree to be updated");
        Assert.assertEquals(nodeSize, newTree.node.size(), "Expected same nodes");
        Assert.assertNotEquals(nodeMeanDepth, newTree.node.meanDepth(), "Expected new mean depth");

        nodeSize = newTree.node.size();
        nodeMeanDepth = newTree.node.meanDepth();
        wb.stripData(newTree);
        Assert.assertEquals(nodeSize, newTree.node.size(), "Expected same nodes");
        Assert.assertEquals(nodeMeanDepth, newTree.node.meanDepth(), "Expected same mean depth");
    }

    @Test
    public void simpleBmiTestRebuild() throws Exception {
        final List<Instance<AttributesMap>> instances = TreeBuilderTestUtils.getInstances(10000);
        final PredictiveModelWithDataBuilder<AttributesMap ,Tree> wb = getWrappedUpdatablePredictiveModelBuilder();
        wb.rebuildThreshold(1);
        final long startTime = System.currentTimeMillis();
        final Tree tree = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.node);

        int nodeSize = tree.node.size();
        double nodeMeanDepth = tree.node.meanDepth();
        Assert.assertTrue(nodeSize < 400, "Tree size should be less than 400 nodes");
        Assert.assertTrue(nodeMeanDepth < 6, "Mean depth should be less than 6");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance<AttributesMap>> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final Tree newTree = wb.buildPredictiveModel(newInstances);
        Assert.assertFalse(tree == newTree, "Expect new tree to be built");
    }

    private PredictiveModelWithDataBuilder<AttributesMap ,Tree> getWrappedUpdatablePredictiveModelBuilder() {
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        return new PredictiveModelWithDataBuilder<>(tb);
    }

    @Test
    public void twoDeterministicDecisionTreesAreEqual() throws IOException, ClassNotFoundException {

        final List<Instance<AttributesMap>> instancesTrain = TreeBuilderTestUtils.getInstances(10000);
        MapUtils.random.setSeed(1l);
        final Tree tree1 = (new TreeBuilder(new SplitDiffScorer())).buildPredictiveModel(instancesTrain);
        MapUtils.random.setSeed(1l);
        final Tree tree2 = (new TreeBuilder(new SplitDiffScorer())).buildPredictiveModel(instancesTrain);

        TreeBuilderTestUtils.serializeDeserialize(tree1.node);
        TreeBuilderTestUtils.serializeDeserialize(tree2.node);
        Assert.assertTrue(tree1.node.size() == tree2.node.size(), "Deterministic Decision Trees must have same number of nodes");

        final List<Instance<AttributesMap>> instancesTest = TreeBuilderTestUtils.getInstances(1000);
        for (Instance<AttributesMap> instance : instancesTest) {
           PredictionMap map1 = tree1.predict(instance.getAttributes());
           PredictionMap map2 = tree2.predict(instance.getAttributes());
            Assert.assertTrue(map1.equals(map2), "Deterministic Decision Trees must have equal classifications");
        }
    }

}
