package quickml.supervised;

import com.beust.jcommander.internal.Lists;
import org.junit.Before;
import org.junit.Test;
import quickml.InstanceLoader;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldTreeBuilder;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers.GiniImpurityOldScorer;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;
import quickml.supervised.tree.decisionTree.scorers.GiniImpurityScorer;

import java.util.List;

/**
 * Created by alexanderhawk on 7/7/15.
 */
public class OldTreeBuildTimeTest {
    private List<ClassifierInstance>  newInstances = Lists.newArrayList();

    @Before
    public void setUp() throws Exception {
        List<ClassifierInstance> instances = InstanceLoader.getAdvertisingInstances();
        for (int i = 0; i < 10; i++) {
            newInstances.addAll(instances);
        }
    }
    @Test
     public void timeTest(){
        DecisionTreeBuilder modelBuilder = new DecisionTreeBuilder().scorer(new GiniImpurityScorer()).maxDepth(16).minLeafInstances(0).minAttributeValueOccurences(2).attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7));
        double start  = System.currentTimeMillis();
        for (int i = 0; i<15; i++) {
            modelBuilder.buildPredictiveModel(newInstances);
        }
        double stop = System.currentTimeMillis();
        System.out.println("build time: " + (stop - start));

        OldTreeBuilder oldModelBuilder = new OldTreeBuilder().scorer(new GiniImpurityOldScorer()).maxDepth(16).minCategoricalAttributeValueOccurances(2).attributeIgnoringStrategy(new quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.oldAttributeIgnoringStrategies.IgnoreAttributesWithConstantProbability(0.7));
        start  = System.currentTimeMillis();
        for (int i = 0; i<15; i++) {
            oldModelBuilder.buildPredictiveModel(newInstances);
        }
        stop = System.currentTimeMillis();
        System.out.println("build time old: " + (stop-start));


    }
    }