package quickdt.predictiveModels.randomForest;

import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.Misc;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModelWithDataBuilder;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 5/14/2014.
 */
public class RandomForestBuilderTest {
    @Test
    public void simpleBmiTest() throws Exception {
        final List<Instance<Map<String,Serializable>>> instances = TreeBuilderTestUtils.getInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder rfb = new RandomForestBuilder(tb);
        final long startTime = System.currentTimeMillis();
        final RandomForest randomForest = rfb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final Map<String, Serializable> testAttributes = instances.get(0).getRegressors();
        for (Map.Entry<Serializable, Double> entry : randomForest.predict(testAttributes).entrySet()) {
            Assert.assertEquals(entry.getValue(), randomForest.getProbability(testAttributes, entry.getKey()));
        }
    }

    @Test
    public void simpleBmiTestSplit() throws Exception {
        final List<Instance<Map<String,Serializable>>> instances = TreeBuilderTestUtils.getInstances(10000);
        final PredictiveModelWithDataBuilder<Map<String,Serializable>,RandomForest> wb = getWrappedUpdatablePredictiveModelBuilder();
        wb.splitNodeThreshold(1);
        final long startTime = System.currentTimeMillis();
        final RandomForest randomForest = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        int treeSize = trees.size();
        int firstTreeNodeSize = trees.get(0).node.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance<Map<String,Serializable>>> newInstances = TreeBuilderTestUtils.getInstances(10000);
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

    private PredictiveModelWithDataBuilder<Map<String,Serializable>,RandomForest> getWrappedUpdatablePredictiveModelBuilder() {
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer()).updatable(true);
        final RandomForestBuilder urfb = new RandomForestBuilder(tb);
        return new PredictiveModelWithDataBuilder<>(urfb);
    }

    @Test
    public void simpleBmiTestNoSplit() throws Exception {
        final List<Instance<Map<String,Serializable>>> instances = TreeBuilderTestUtils.getInstances(10000);
        final PredictiveModelWithDataBuilder<Map<String,Serializable>,RandomForest> wb = getWrappedUpdatablePredictiveModelBuilder();
        final long startTime = System.currentTimeMillis();
        final RandomForest randomForest = wb.buildPredictiveModel(instances);

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        int treeSize = trees.size();
        int firstTreeNodeSize = trees.get(0).node.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance<Map<String,Serializable>>> newInstances = TreeBuilderTestUtils.getInstances(10000);
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

    @Test
    public void twoDeterministicRandomForestsAreEqual() throws IOException, ClassNotFoundException {
        final List<Instance<Map<String,Serializable>>> instancesTrain = TreeBuilderTestUtils.getInstances(10000);
        final RandomForestBuilder urfb = new RandomForestBuilder(new TreeBuilder(new SplitDiffScorer()).updatable(true));
        Misc.random.setSeed(1l);
        final RandomForest randomForest1 = urfb.executorThreadCount(1).buildPredictiveModel(instancesTrain);
        Misc.random.setSeed(1l);
        final RandomForest randomForest2 = urfb.executorThreadCount(1).buildPredictiveModel(instancesTrain);

        Assert.assertTrue(randomForest1.trees.size() == randomForest2.trees.size(), "Deterministic Random Forests must have same number of trees");
        for (int i = 0; i < randomForest1.trees.size(); i++) {
            Assert.assertTrue(randomForest1.trees.get(i).node.size() == randomForest2.trees.get(i).node.size(), "Deterministic Decision Trees must have same number of nodes");
        }

        final List<Instance<Map<String,Serializable>>> instancesTest = TreeBuilderTestUtils.getInstances(1000);
        for (Instance<Map<String,Serializable>> instance : instancesTest) {
            Map map1 = randomForest1.predict(instance.getRegressors());
            Map map2 = randomForest2.predict(instance.getRegressors());
            Assert.assertTrue(map1.equals(map2), "Deterministic Decision Trees must have equal classifications");
        }
    }

}
