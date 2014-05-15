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
public class UpdatablePAVCalibratedPredictiveModelBuilderTest {
    @Test
    public void simpleBmiTest() throws Exception {
        final List<Instance> instances = TreeBuilderTestUtils.getIntegerInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder rfb = new RandomForestBuilder(tb);
        final PAVCalibratedPredictiveModelBuilder cpmb = new PAVCalibratedPredictiveModelBuilder(rfb);
        final UpdatablePAVCalibratedPredictiveModelBuilder ucpmb = new UpdatablePAVCalibratedPredictiveModelBuilder(cpmb);
        final long startTime = System.currentTimeMillis();
        final CalibratedPredictiveModel calibratedPredictiveModel = ucpmb.buildPredictiveModel(instances);
        final RandomForest randomForest = (RandomForest) calibratedPredictiveModel.predictiveModel;

        TreeBuilderTestUtils.serializeDeserialize(randomForest);

        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        final int firstTreeNodeSize = trees.get(0).node.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");

        final List<Instance> newInstances = TreeBuilderTestUtils.getIntegerInstances(10000);
        final CalibratedPredictiveModel newCalibratedPredictiveModel = ucpmb.buildPredictiveModel(newInstances);
        final RandomForest newRandomForest = (RandomForest) newCalibratedPredictiveModel.predictiveModel;
        Assert.assertTrue(calibratedPredictiveModel == newCalibratedPredictiveModel, "Expect same tree to be updated");
        Assert.assertEquals(treeSize, newRandomForest.trees.size(), "Expected same number of trees");
        Assert.assertNotEquals(firstTreeNodeSize, newRandomForest.trees.get(0).node.size(), "Expected new nodes");
    }
}
