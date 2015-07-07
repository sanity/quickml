package quickml.supervised.tree.nodes;

import org.junit.Before;
import org.junit.Test;
import quickml.InstanceLoader;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;
import quickml.supervised.tree.decisionTree.scorers.GiniImpurityScorer;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by alexanderhawk on 7/7/15.
 */
public class OldLeafDepthStatsTest {

    private List<ClassifierInstance> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances();
    }

    @Test
    public void calcMeanAndMedianDepth(){
        System.out.println("\n \n \n new  attrImportanceTest");
        DecisionTreeBuilder modelBuilder = new DecisionTreeBuilder().scorer(new GiniImpurityScorer()).maxDepth(16).minLeafInstances(0).minAttributeValueOccurences(2).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7));


        int depthZeros = 0;
        double meanDepthT = 0;
        double medianDepthT = 0;
        for (int i = 0; i<2; i++) {
            LeafDepthStats statsL = new LeafDepthStats();
            DecisionTree tree = modelBuilder.buildPredictiveModel(instances);
            tree.root.calcLeafDepthStats(statsL);
            medianDepthT+=tree.calcMedianDepth();
            double meanDepthL = ((1.0)*statsL.ttlDepth)/statsL.ttlSamples ;
            meanDepthT += meanDepthL;
            if (meanDepthL < 1E-5) {
                depthZeros++;
            }

        }
        System.out.println("depth zeros " + depthZeros + "mean depth: " + meanDepthT/2.0 + " median depth: " + medianDepthT/2.0);
    }

}