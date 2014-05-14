package quickdt.predictiveModels.calibratedPredictiveModel;


import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.data.Instance;
import quickdt.predictiveModels.TreeBuilderTestUtils;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.util.List;

/**
 * Created by Chris on 5/14/2014.
 */
public class PAVCalibratedPredictiveModelBuilderTest {
    @Test
    public void simpleBmiTest() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getIntegerInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder rfb = new RandomForestBuilder(tb);
        final PAVCalibratedPredictiveModelBuilder cpmb = new PAVCalibratedPredictiveModelBuilder(rfb);
        final long startTime = System.currentTimeMillis();
        final CalibratedPredictiveModel calibratedPredictiveModel = cpmb.buildPredictiveModel(instances);
        final RandomForest randomForest = (RandomForest) calibratedPredictiveModel.predictiveModel;

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        final double treeMeanDepth = trees.get(0).node.meanDepth();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getIntegerInstances(1000);
        cpmb.updatePredictiveModel(calibratedPredictiveModel, newInstances);
        Assert.assertEquals(treeSize, trees.size(), "Forest size should be the same after update");
        Assert.assertNotEquals(treeMeanDepth, trees.get(0).node.meanDepth(), "Mean depth should change after update");
    }
}
