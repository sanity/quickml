package quickml.supervised.tree.nodes;

import org.junit.Before;
import org.junit.Test;
import quickml.InstanceLoader;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldTree;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldTreeBuilder;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers.GiniImpurityOldScorer;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;
import quickml.supervised.tree.decisionTree.scorers.GRPenalizedGiniImpurityScorer;

import java.util.List;

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
        DecisionTreeBuilder modelBuilder = new DecisionTreeBuilder().scorerFactory(new GRPenalizedGiniImpurityScorer()).maxDepth(16).minLeafInstances(0).minAttributeValueOccurences(2).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7));


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

        System.out.println("\n \n \n new  attrImportanceTest\n\n\n");
        OldTreeBuilder oldModelBuilder = new OldTreeBuilder().scorer(new GiniImpurityOldScorer()).maxDepth(16).minCategoricalAttributeValueOccurances(2).attributeIgnoringStrategy(new quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.oldAttributeIgnoringStrategies.IgnoreAttributesWithConstantProbability(0.7));

        depthZeros = 0;
        meanDepthT = 0;
        medianDepthT = 0;
        for (int i = 0; i<2; i++) {
            OldTree oldTree = oldModelBuilder.buildPredictiveModel(instances);
            double meanDepthI = oldTree.oldNode.meanDepth();
            meanDepthT +=meanDepthI;
            medianDepthT += oldTree.oldNode.medianDepth();
            if (meanDepthI < 1E-5) {
                depthZeros++;
            }
        }
        System.out.println("Old Model info: depth zeros " + depthZeros + "mean depth: " + meanDepthT/2.0 + " median depth: " + medianDepthT/2.0);
    }

}