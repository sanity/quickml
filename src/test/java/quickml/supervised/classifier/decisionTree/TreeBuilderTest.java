package quickml.supervised.classifier.decisionTree;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.decisionTree.tree.Node;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class TreeBuilderTest {

    @Test
    public void simpleBmiTest() throws Exception {
        final List<ClassifierInstance> instances = TreeBuilderTestUtils.getInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final long startTime = System.currentTimeMillis();
        final Tree tree = tb.buildPredictiveModel(instances);
        final Node node = tree.root;

        TreeBuilderTestUtils.serializeDeserialize(node);

        final int nodeSize = node.size();
        assertTrue("Tree size should be less than 400 nodes", nodeSize < 400);
        assertTrue("Building this root should take far less than 20 seconds", (System.currentTimeMillis() - startTime) < 20000);
    }


    @Test
    public void multiScorerBmiTest() {
        final Set<ClassifierInstance> instances = Sets.newHashSet();

        for (int x = 0; x < 10000; x++) {
            final double height = (4 * 12) + MapUtils.random.nextInt(3 * 12);
            final double weight = 120 + MapUtils.random.nextInt(110);
            AttributesMap attributes = AttributesMap.newHashMap();
            attributes.put("weight", weight);
            attributes.put("height", height);
            final ClassifierInstance instance = new ClassifierInstance(attributes, TreeBuilderTestUtils.bmiHealthy(weight, height));
            instances.add(instance);
        }
        {
            final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
            final Tree tree = tb.buildPredictiveModel(instances);
            System.out.println("SplitDiffScorer root size: " + tree.root.size());
        }
    }

    @Test
    public void treeMadeExpectedSplits() {
        final List<ClassifierInstance> instances = Lists.newArrayList();
        //TODO: make code that generates test instances like these automatic
        AttributesMap attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", true);
        instances.add(new ClassifierInstance(attributesMap, 1.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", true);
        instances.add(new ClassifierInstance(attributesMap, 0.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", false);
        instances.add(new ClassifierInstance(attributesMap, 0.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", false);
        instances.add(new ClassifierInstance(attributesMap, 0.0));

        TreeBuilder treeBuilder = new TreeBuilder().minCategoricalAttributeValueOccurances(0).minLeafInstances(0);
        Tree tree = treeBuilder.buildPredictiveModel(instances);
        AttributesMap attributes = new AttributesMap();
        attributes.put("a", true);
        assertEquals(tree.getProbability(attributes, 1.0), .5, 0.01);
        attributes.put("a", false);
        assertEquals(tree.getProbability(attributes, 0.0), 1.0, 0.01);

    }

    //TODO: fails randomly.  fix it.
    @Ignore
    @Test
    public void simpleBmiTestSplit() throws Exception {
        final List<ClassifierInstance> instances = TreeBuilderTestUtils.getInstances(10000);
        TreeBuilder treeBuilder = createTreeBuilder();
        final long startTime = System.currentTimeMillis();
        final Tree tree = treeBuilder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.root);

        int nodeSize = tree.root.size();
        double nodeMeanDepth = tree.root.meanDepth();
        assertTrue("Tree size should be less than 400 nodes", nodeSize < 400);
        assertTrue("Mean depth should be less than 6", nodeMeanDepth < 6);
        assertTrue("Building this root should take far less than 20 seconds", (System.currentTimeMillis() - startTime) < 20000);

        final List<ClassifierInstance> newInstances = TreeBuilderTestUtils.getInstances(1000);
        final Tree newTree = treeBuilder.buildPredictiveModel(newInstances);
        assertTrue("Expect same tree to be updated", tree == newTree);
        assertNotSame("Expected new nodes", nodeSize, newTree.root.size());
        assertNotSame("Expected new mean depth", nodeMeanDepth, newTree.root.meanDepth());
    }

    @Ignore("Test is currently failing, but is really testing updating of a predictive model, which 1. has been removed and 2. doesn't belong here")
    @Test
    public void simpleBmiTestNoSplit() throws Exception {
        final List<ClassifierInstance> instances = TreeBuilderTestUtils.getInstances(10000);
        TreeBuilder treeBuilder = createTreeBuilder();
        final long startTime = System.currentTimeMillis();
        final Tree tree = treeBuilder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.root);

        int nodeSize = tree.root.size();
        double nodeMeanDepth = tree.root.meanDepth();
        assertTrue("Tree size should be less than 400 nodes", nodeSize < 400);
        assertTrue("Mean depth should be less than 6", nodeMeanDepth < 6);
        assertTrue("Building this root should take far less than 20 seconds", (System.currentTimeMillis() - startTime) < 20000);

        final List<ClassifierInstance> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final Tree newTree = treeBuilder.buildPredictiveModel(newInstances);
        assertEquals("Expected same nodes", nodeSize, newTree.root.size());
        assertNotSame("Expected new mean depth", nodeMeanDepth, newTree.root.meanDepth());

    }

    @Test
    public void simpleBmiTestRebuild() throws Exception {
        final List<ClassifierInstance> instances = TreeBuilderTestUtils.getInstances(10000);
        TreeBuilder treeBuilder = createTreeBuilder();
        final long startTime = System.currentTimeMillis();
        final Tree tree = treeBuilder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(tree.root);

        int nodeSize = tree.root.size();
        double nodeMeanDepth = tree.root.meanDepth();
        assertTrue("Tree size should be less than 400 nodes", nodeSize < 400);
        assertTrue("Mean depth should be less than 6", nodeMeanDepth < 6);
        assertTrue("Building this root should take far less than 20 seconds", (System.currentTimeMillis() - startTime) < 20000);

        final List<ClassifierInstance> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final Tree newTree = treeBuilder.buildPredictiveModel(newInstances);
        Assert.assertFalse("Expect new tree to be built", tree == newTree);
    }

    private TreeBuilder createTreeBuilder() {
        return new TreeBuilder(new SplitDiffScorer());
    }

    @Test
    public void twoDeterministicDecisionTreesAreEqual() throws IOException, ClassNotFoundException {

        int numSamples = 1000;
        final List<ClassifierInstance> instancesTrain = TreeBuilderTestUtils.getInstances(numSamples);
        MapUtils.random.setSeed(1l);
        final Tree tree1 = (new TreeBuilder(new SplitDiffScorer()).numSamplesForComputingNumericSplitPoints(numSamples)).buildPredictiveModel(instancesTrain);
        MapUtils.random.setSeed(1l);
        final Tree tree2 = (new TreeBuilder(new SplitDiffScorer()).numSamplesForComputingNumericSplitPoints(numSamples)).buildPredictiveModel(instancesTrain);

        TreeBuilderTestUtils.serializeDeserialize(tree1.root);
        TreeBuilderTestUtils.serializeDeserialize(tree2.root);
        assertTrue("Deterministic Decision Trees must have same number of nodes", tree1.root.size() == tree2.root.size());

        final List<ClassifierInstance> instancesTest = TreeBuilderTestUtils.getInstances(1000);
        for (ClassifierInstance instance : instancesTest) {
            PredictionMap map1 = tree1.predict(instance.getAttributes());
            PredictionMap map2 = tree2.predict(instance.getAttributes());
            assertTrue("Deterministic Decision Trees must have equal classifications", map1.equals(map2));
        }
    }

}
