package quickml.decisionTree;

import com.beust.jcommander.internal.Lists;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.ClassifierInstance;
import quickml.InstanceLoader;
import quickml.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;

import java.util.List;

/**
 * Created by alexanderhawk on 4/26/15.
 */
public class DecisionTreeContextBuilderTest {
    @Test
    public void firstTest() {
        DecisionTreeBuilder<ClassifierInstance> decisionTreeBuilder = new DecisionTreeBuilder<>().numSamplesPerNumericBin(50).numNumericBins(6)
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7)).maxDepth(5).minSplitFraction(0.1)
                .degreeOfGainRatioPenalty(1.0).minAttributeOccurences(8);
        ;
        List<ClassifierInstance> instances = Lists.newArrayList(InstanceLoader.getAdvertisingInstances()).subList(0, 1000);
        DecisionTree decisionTree = decisionTreeBuilder.buildPredictiveModel(instances);
        Assert.assertTrue("hello", true);
    }
}