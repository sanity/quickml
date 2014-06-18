package quickdt.predictiveModels.splitOnAttributePredictiveModel;


import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModelWithDataBuilder;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Chris on 5/14/2014.
 */
public class SplitOnAttributePMBuilderTest {
    @Test
    public void simpleBmiTest() throws Exception {
        Set<String> whiteList = new HashSet<>();
        whiteList.add("weight");
        whiteList.add("height");
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer()).splitPredictiveModel("gender", whiteList);
        final RandomForestBuilder rfb = new RandomForestBuilder(tb);
        final SplitOnAttributePMBuilder cpmb = new SplitOnAttributePMBuilder("gender", rfb, 10, 0.1, whiteList, 1);
        final long startTime = System.currentTimeMillis();
        final SplitOnAttributePM splitOnAttributePM = cpmb.buildPredictiveModel(instances);
        final RandomForest randomForest = (RandomForest) splitOnAttributePM.getDefaultPM();

        TreeBuilderTestUtils.serializeDeserialize(randomForest);
        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");
    }

    @Test
    public void simpleBmiTestSplit() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(1000);
        final PredictiveModelWithDataBuilder<SplitOnAttributePM > wb = getWrappedUpdatablePredictiveModelBuilder();
        wb.splitNodeThreshold(1);
        final long startTime = System.currentTimeMillis();
        final SplitOnAttributePM  splitOnAttributePM = wb.buildPredictiveModel(instances);
        final RandomForest randomForest = (RandomForest) splitOnAttributePM.getDefaultPM();

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        final int firstTreeNodeSize = trees.get(0).node.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getInstances(1000);
        final SplitOnAttributePM  splitOnAttributePM1 = wb.buildPredictiveModel(newInstances);
        final RandomForest newRandomForest = (RandomForest) splitOnAttributePM1.getDefaultPM();
        Assert.assertTrue(splitOnAttributePM == splitOnAttributePM1, "Expect same tree to be updated");
        Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same number of trees");
        Assert.assertNotEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected new nodes");
    }

    @Test
    public void simpleBmiTestNoSplit() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getInstances(1000);
        final PredictiveModelWithDataBuilder<SplitOnAttributePM > wb = getWrappedUpdatablePredictiveModelBuilder();
        final long startTime = System.currentTimeMillis();
        final SplitOnAttributePM  splitOnAttributePM = wb.buildPredictiveModel(instances);
        final RandomForest randomForest = (RandomForest) splitOnAttributePM.getDefaultPM();

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        final int firstTreeNodeSize = trees.get(0).node.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getInstances(1000);
        final SplitOnAttributePM splitOnAttributePM1 = wb.buildPredictiveModel(newInstances);
        final RandomForest newRandomForest = (RandomForest) splitOnAttributePM1.getDefaultPM();
        Assert.assertTrue(splitOnAttributePM == splitOnAttributePM1, "Expect same tree to be updated");
        Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same number of trees");
        Assert.assertEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected same nodes");
    }

    private PredictiveModelWithDataBuilder<SplitOnAttributePM > getWrappedUpdatablePredictiveModelBuilder() {
        Set<String> whiteList = new HashSet<>();
        whiteList.add("weight");
        whiteList.add("height");
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer()).splitPredictiveModel("gender", whiteList);
        final RandomForestBuilder urfb = new RandomForestBuilder(tb);
        final SplitOnAttributePMBuilder ucpmb = new SplitOnAttributePMBuilder("gender", urfb, 10, 0.1, whiteList, 1);
        return new PredictiveModelWithDataBuilder<>(ucpmb);
    }
}
