package quickml.supervised.classifier.randomForest;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final AttributesMap testAttributes = instances.get(0).getAttributes();
        for (Map.Entry<Serializable, Double> entry : randomForest.predict(testAttributes).entrySet()) {
            Assert.assertEquals(entry.getValue(), randomForest.getProbability(testAttributes, entry.getKey()));
        }
    }



    @Test
    public void twoDeterministicRandomForestsAreEqual() throws IOException, ClassNotFoundException {
        final List<ClassifierInstance> instancesTrain = TreeBuilderTestUtils.getInstances(10000);
        final RandomForestBuilder urfb = new RandomForestBuilder(new TreeBuilder(new SplitDiffScorer()));
        MapUtils.random.setSeed(1l);
        final RandomForest randomForest1 = urfb.executorThreadCount(1).buildPredictiveModel(instancesTrain);
        MapUtils.random.setSeed(1l);
        final RandomForest randomForest2 = urfb.executorThreadCount(1).buildPredictiveModel(instancesTrain);

        Assert.assertTrue(randomForest1.trees.size() == randomForest2.trees.size(), "Deterministic Random Forests must have same number of trees");
        for (int i = 0; i < randomForest1.trees.size(); i++) {
            Assert.assertTrue(randomForest1.trees.get(i).node.size() == randomForest2.trees.get(i).node.size(), "Deterministic Decision Trees must have same number of nodes");
        }

        final List<ClassifierInstance> instancesTest = TreeBuilderTestUtils.getInstances(1000);
        for (ClassifierInstance instance : instancesTest) {
           PredictionMap map1 = randomForest1.predict(instance.getAttributes());
           PredictionMap map2 = randomForest2.predict(instance.getAttributes());
            Assert.assertTrue(map1.equals(map2), "Deterministic Decision Trees must have equal classifications");
        }
    }

}
