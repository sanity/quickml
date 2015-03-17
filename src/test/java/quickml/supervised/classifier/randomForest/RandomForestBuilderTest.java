package quickml.supervised.classifier.randomForest;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.decisionTree.tree.*;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static quickml.supervised.InstanceLoader.getAdvertisingInstances;

public class RandomForestBuilderTest {
    @Test
    public void simpleBmiTest() throws Exception {
        final List<ClassifierInstance> instances = TreeBuilderTestUtils.getInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder rfb = new RandomForestBuilder(tb);
        final long startTime = System.currentTimeMillis();
        final RandomForest randomForest = rfb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this root should take far less than 20 seconds");

        final AttributesMap testAttributes = instances.get(0).getAttributes();
        for (Map.Entry<Serializable, Double> entry : randomForest.predict(testAttributes).entrySet()) {
            Assert.assertEquals(entry.getValue(), randomForest.getProbability(testAttributes, entry.getKey()));
        }
    }



    @Test
    public void twoDeterministicTreesinAForestsAreEqual() throws IOException, ClassNotFoundException {
        final List<ClassifierInstance> instancesTrain =  getAdvertisingInstances();

        TreeBuilder treeBuilder = new TreeBuilder(new GiniImpurityScorer())
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.0))
                .maxDepth(10).numSamplesForComputingNumericSplitPoints(instancesTrain.size());
        final RandomForestBuilder<ClassifierInstance> urfb = new RandomForestBuilder<>(treeBuilder).numTrees(2);
        MapUtils.random.setSeed(1l);
        final RandomForest randomForest1 = urfb.executorThreadCount(1).buildPredictiveModel(instancesTrain);


        Node root1 = randomForest1.trees.get(0).root;
        Node root2 = randomForest1.trees.get(1).root;
        traverseTree(root1, root2);
    }

    private void traverseTree(Node node0, Node node1) {
        compareNode(node0, node1);
        if (node0 instanceof Branch && node1 instanceof Branch) {
            traverseTree(((Branch) node0).falseChild, ((Branch) node1).falseChild);
            traverseTree(((Branch) node0).trueChild, ((Branch) node1).trueChild);

        }
        if (node0 instanceof Branch)
            Assert.assertTrue(node1 instanceof Branch);
        if (node1 instanceof Branch)
            Assert.assertTrue(node0 instanceof Branch);

    }

    private void compareNode(Node node0, Node node1) {
        if (node0 instanceof CategoricalBranch)  {
            CategoricalBranch categoricalBranch1 = (CategoricalBranch)node0;
            CategoricalBranch categoricalBranch2 = (CategoricalBranch)node1;
            Set<Serializable> inset1 = categoricalBranch1.inSet;
            Set<Serializable> inset2= categoricalBranch2.inSet;


            Assert.assertTrue(inset1.containsAll(inset2) && inset2.containsAll(inset1));
        }

        else if (node0 instanceof NumericBranch) {
            NumericBranch numericBranch1 = (NumericBranch)node0;
            NumericBranch numericBranch2 = (NumericBranch)node1;
            double threshold1 = numericBranch1.threshold;
            double threshold2= numericBranch2.threshold;


            Assert.assertEquals(threshold1, threshold2);
        }
        else if (node0 instanceof Leaf) {
            Leaf leaf1 = (Leaf)node0;
            Leaf leaf2 = (Leaf)node1;
            double posProb1 = leaf1.getProbability(1.0);
            double posProb2= leaf2.getProbability(1.0);
            Assert.assertEquals(posProb1, posProb2);
        }
    }

    @Test
    public void twoDeterministicRandomForestsAreEqual() throws IOException, ClassNotFoundException {
        final List<ClassifierInstance> instancesTrain = TreeBuilderTestUtils.getInstances(10000);
        final RandomForestBuilder urfb = new RandomForestBuilder(new TreeBuilder(new SplitDiffScorer()).numSamplesForComputingNumericSplitPoints(instancesTrain.size()));
        MapUtils.random.setSeed(1l);
        final RandomForest randomForest1 = urfb.executorThreadCount(1).buildPredictiveModel(instancesTrain);
        MapUtils.random.setSeed(1l);
        final RandomForest randomForest2 = urfb.executorThreadCount(1).buildPredictiveModel(instancesTrain);

        Assert.assertTrue(randomForest1.trees.size() == randomForest2.trees.size(), "Deterministic Random Forests must have same number of trees");
        for (int i = 0; i < randomForest1.trees.size(); i++) {
            Assert.assertTrue(randomForest1.trees.get(i).root.size() == randomForest2.trees.get(i).root.size(), "Deterministic Decision Trees must have same number of nodes");
        }

        final List<ClassifierInstance> instancesTest = TreeBuilderTestUtils.getInstances(1000);
        for (ClassifierInstance instance : instancesTest) {
            PredictionMap map1 = randomForest1.predict(instance.getAttributes());
            PredictionMap map2 = randomForest2.predict(instance.getAttributes());
            Assert.assertTrue(map1.equals(map2), "Deterministic Decision Trees must have equal classifications");
        }
    }
}
