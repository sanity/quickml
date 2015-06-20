package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.supervised.tree.scorers.SplitDiffScorer;
import quickml.supervised.tree.decisionTree.tree.Node;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


public class DecisionTreeBuilderTest {

    @Test
    public void simpleBmiTest() throws Exception {
        final List<InstanceWithAttributesMap> instances = TreeBuilderTestUtils.getInstances(10000);
        final TreeBuilderHelper tb = new TreeBuilderHelper(new SplitDiffScorer());
        final long startTime = System.currentTimeMillis();
        final DecisionTree decisionTree = tb.buildPredictiveModel(instances);
        final Node node = decisionTree.root;

        TreeBuilderTestUtils.serializeDeserialize(node);

        final int nodeSize = node.size();
        assertTrue("Tree getSize should be less than 400 nodes", nodeSize < 400);
        assertTrue("Building this root should take far less than 20 seconds", (System.currentTimeMillis() - startTime) < 20000);
    }


    @Test
    public void multiScorerBmiTest() {
        final Set<InstanceWithAttributesMap> instances = Sets.newHashSet();

        for (int x = 0; x < 10000; x++) {
            final double height = (4 * 12) + MapUtils.random.nextInt(3 * 12);
            final double weight = 120 + MapUtils.random.nextInt(110);
            AttributesMap attributes = AttributesMap.newHashMap();
            attributes.put("weight", weight);
            attributes.put("height", height);
            final InstanceWithAttributesMap instance = new InstanceWithAttributesMap(attributes, TreeBuilderTestUtils.bmiHealthy(weight, height));
            instances.add(instance);
        }
        {
            final TreeBuilderHelper tb = new TreeBuilderHelper(new SplitDiffScorer());
            final DecisionTree decisionTree = tb.buildPredictiveModel(instances);
            System.out.println("SplitDiffScorer root getSize: " + decisionTree.root.getSize());
        }
    }

    @Test
    public void treeMadeExpectedSplits() {
        final List<InstanceWithAttributesMap> instances = Lists.newArrayList();
        //TODO: make code that generates test instances like these automatic
        AttributesMap attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", true);
        instances.add(new InstanceWithAttributesMap(attributesMap, 1.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", true);
        instances.add(new InstanceWithAttributesMap(attributesMap, 0.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", false);
        instances.add(new InstanceWithAttributesMap(attributesMap, 0.0));
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", false);
        instances.add(new InstanceWithAttributesMap(attributesMap, 0.0));

        TreeBuilderHelper treeBuilder = new TreeBuilderHelper().minCategoricalAttributeValueOccurances(0).minLeafInstances(0);
        DecisionTree decisionTree = treeBuilder.buildPredictiveModel(instances);
        AttributesMap attributes = new AttributesMap();
        attributes.put("a", true);
        assertEquals(decisionTree.getProbability(attributes, 1.0), .5, 0.01);
        attributes.put("a", false);
        assertEquals(decisionTree.getProbability(attributes, 0.0), 1.0, 0.01);

    }

    //TODO: fails randomly.  fix it.
    @Ignore
    @Test
    public void simpleBmiTestSplit() throws Exception {
        final List<InstanceWithAttributesMap> instances = TreeBuilderTestUtils.getInstances(10000);
        TreeBuilderHelper treeBuilder = createTreeBuilder();
        final long startTime = System.currentTimeMillis();
        final DecisionTree decisionTree = treeBuilder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(decisionTree.root);

        int nodeSize = decisionTree.root.getSize();
        double nodeMeanDepth = decisionTree.root.meanDepth();
        assertTrue("Tree getSize should be less than 400 nodes", nodeSize < 400);
        assertTrue("Mean depth should be less than 6", nodeMeanDepth < 6);
        assertTrue("Building this root should take far less than 20 seconds", (System.currentTimeMillis() - startTime) < 20000);

        final List<InstanceWithAttributesMap> newInstances = TreeBuilderTestUtils.getInstances(1000);
        final DecisionTree newDecisionTree = treeBuilder.buildPredictiveModel(newInstances);
        assertTrue("Expect same tree to be updated", decisionTree == newDecisionTree);
        assertNotSame("Expected new nodes", nodeSize, newDecisionTree.root.getSize());
        assertNotSame("Expected new mean depth", nodeMeanDepth, newDecisionTree.root.meanDepth());
    }

    @Ignore("Test is currently failing, but is really testing updating of a predictive model, which 1. has been removed and 2. doesn't belong here")
    @Test
    public void simpleBmiTestNoSplit() throws Exception {
        final List<InstanceWithAttributesMap> instances = TreeBuilderTestUtils.getInstances(10000);
        TreeBuilderHelper treeBuilder = createTreeBuilder();
        final long startTime = System.currentTimeMillis();
        final DecisionTree decisionTree = treeBuilder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(decisionTree.root);

        int nodeSize = decisionTree.root.getSize();
        double nodeMeanDepth = decisionTree.root.meanDepth();
        assertTrue("Tree getSize should be less than 400 nodes", nodeSize < 400);
        assertTrue("Mean depth should be less than 6", nodeMeanDepth < 6);
        assertTrue("Building this root should take far less than 20 seconds", (System.currentTimeMillis() - startTime) < 20000);

        final List<InstanceWithAttributesMap> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final DecisionTree newDecisionTree = treeBuilder.buildPredictiveModel(newInstances);
        assertEquals("Expected same nodes", nodeSize, newDecisionTree.root.getSize());
        assertNotSame("Expected new mean depth", nodeMeanDepth, newDecisionTree.root.meanDepth());

    }

    @Test
    public void simpleBmiTestRebuild() throws Exception {
        final List<InstanceWithAttributesMap> instances = TreeBuilderTestUtils.getInstances(10000);
        TreeBuilderHelper treeBuilder = createTreeBuilder();
        final long startTime = System.currentTimeMillis();
        final DecisionTree decisionTree = treeBuilder.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(decisionTree.root);

        int nodeSize = decisionTree.root.getSize();
        double nodeMeanDepth = decisionTree.root.meanDepth();
        assertTrue("Tree getSize should be less than 400 nodes", nodeSize < 400);
        assertTrue("Mean depth should be less than 6", nodeMeanDepth < 6);
        assertTrue("Building this root should take far less than 20 seconds", (System.currentTimeMillis() - startTime) < 20000);

        final List<InstanceWithAttributesMap> newInstances = TreeBuilderTestUtils.getInstances(10000);
        final DecisionTree newDecisionTree = treeBuilder.buildPredictiveModel(newInstances);
        Assert.assertFalse("Expect new tree to be built", decisionTree == newDecisionTree);
    }

    private TreeBuilderHelper createTreeBuilder() {
        return new TreeBuilderHelper(new SplitDiffScorer());
    }

    @Test
    public void twoDeterministicDecisionTreesAreEqual() throws IOException, ClassNotFoundException {

        int numSamples = 1000;
        final List<InstanceWithAttributesMap> instancesTrain = TreeBuilderTestUtils.getInstances(numSamples);
        MapUtils.random.setSeed(1l);
        final DecisionTree decisionTree1 = (new TreeBuilderHelper(new SplitDiffScorer()).numSamplesForComputingNumericSplitPoints(numSamples)).buildPredictiveModel(instancesTrain);
        MapUtils.random.setSeed(1l);
        final DecisionTree decisionTree2 = (new TreeBuilderHelper(new SplitDiffScorer()).numSamplesForComputingNumericSplitPoints(numSamples)).buildPredictiveModel(instancesTrain);

        TreeBuilderTestUtils.serializeDeserialize(decisionTree1.root);
        TreeBuilderTestUtils.serializeDeserialize(decisionTree2.root);
        assertTrue("Deterministic Decision Trees must have same number of nodes", decisionTree1.root.getSize() == decisionTree2.root.getSize());

        final List<InstanceWithAttributesMap> instancesTest = TreeBuilderTestUtils.getInstances(1000);
        for (InstanceWithAttributesMap instance : instancesTest) {
            PredictionMap map1 = decisionTree1.predict(instance.getAttributes());
            PredictionMap map2 = decisionTree2.predict(instance.getAttributes());
            assertTrue("Deterministic Decision Trees must have equal classifications", map1.equals(map2));
        }
    }

}
