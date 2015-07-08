package quickml.supervised.crossValidation;

import org.junit.Before;
import org.junit.Test;
import quickml.InstanceLoader;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldTreeBuilder;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers.GiniImpurityOldScorer;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.oldAttributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.crossValidation.data.OutOfTimeData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;
import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;
import quickml.supervised.tree.decisionTree.scorers.GiniImpurityScorer;

import java.util.List;

/**
 * Created by alexanderhawk on 7/8/15.
 */
public class CrossValidatorIntegrationTest {

    private List<ClassifierInstance> instances;

    @Before
    public void setUp() throws Exception {
        instances = InstanceLoader.getAdvertisingInstances().subList(0,1000);
    }


    @Test
    public void testCrossValidation() throws Exception {
        System.out.println("\n \n \n new  attrImportanceTest");
        DecisionTreeBuilder<ClassifierInstance> modelBuilder = new DecisionTreeBuilder<ClassifierInstance>().scorer(new GiniImpurityScorer()).maxDepth(16).minLeafInstances(0).minAttributeValueOccurences(11).attributeIgnoringStrategy(new quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability(0.7));

        CrossValidator<DecisionTree, ClassifierInstance> cv = new CrossValidator<>(modelBuilder,
                new ClassifierLossChecker<ClassifierInstance, DecisionTree>(new ClassifierLogCVLossFunction(.000001)),
                new OutOfTimeData<>(instances, .25, 12, new OnespotDateTimeExtractor() ) );
        for (int i =0; i<3; i++) {
            System.out.println("Loss: " + cv.getLossForModel());
        }

    }
}
