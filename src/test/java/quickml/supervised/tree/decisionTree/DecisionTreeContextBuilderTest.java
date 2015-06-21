package quickml.supervised.tree.decisionTree;

import com.beust.jcommander.internal.Lists;
import junit.framework.TestCase;
import quickml.data.ClassifierInstance;
import quickml.supervised.InstanceLoader;
import quickml.supervised.tree.scorers.GiniImpurityScorer;

import java.util.List;

/**
 * Created by alexanderhawk on 4/26/15.
 */
public class DecisionTreeContextBuilderTest extends TestCase {
    //limitations: setting ordinalSplits, setting split propert
//baagging should be true or false and ovverloaded with an option to pass in bagging object
    DecisionTreeBuildContext decisionTreeBuildContext = new DecisionTreeBuildContext().
            bagging(false).
            scorer(new GiniImpurityScorer()).
            terminationConditions(new StandardTerminationConditions().maxDepth(10).minLeafInstances(10).minScore(0.0000001));
    DecisionTreeBuilderHelper<ClassifierInstance> decisionTreeBuilder = new DecisionTreeBuilderHelper<>(decisionTreeBuildContext);
    List<ClassifierInstance> instances = Lists.newArrayList(InstanceLoader.getAdvertisingInstances()).subList(0, 1000);
    DecisionTree decisionTree = decisionTreeBuilder.buildPredictiveModel(instances);

}