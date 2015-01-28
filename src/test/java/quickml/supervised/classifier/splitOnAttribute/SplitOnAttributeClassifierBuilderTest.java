package quickml.supervised.classifier.splitOnAttribute;


import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.classifier.TreeBuilderTestUtils;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SplitOnAttributeClassifierBuilderTest {
    @Test
    public void simpleBmiTest() throws Exception {
        Set<String> whiteList = new HashSet<>();
        whiteList.add("weight");
        whiteList.add("height");
        final List<ClassifierInstance> instances = TreeBuilderTestUtils.getInstances(10000);
        final TreeBuilder tb = new TreeBuilder(new SplitDiffScorer());
        final RandomForestBuilder rfb = new RandomForestBuilder(tb);
        final SplitOnAttributeClassifierBuilder cpmb = new SplitOnAttributeClassifierBuilder("gender", rfb, 10, 0.1, whiteList, 1);
        final long startTime = System.currentTimeMillis();
        final SplitOnAttributeClassifier splitOnAttributeClassifier = cpmb.buildPredictiveModel(instances);
        final RandomForest randomForest = (RandomForest) splitOnAttributeClassifier.getDefaultPM();

        TreeBuilderTestUtils.serializeDeserialize(randomForest);
        final List<Tree> trees = randomForest.trees;
        final int treeSize = trees.size();
        Assert.assertTrue(treeSize < 400, "Forest size should be less than 400");
        Assert.assertTrue((System.currentTimeMillis() - startTime) < 20000,"Building this node should take far less than 20 seconds");
    }
}
