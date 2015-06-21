package quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.DTBinaryCatBranchFinder;
import quickml.supervised.tree.decisionTree.branchFinders.DTreeNClassCatBranchFinder;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.decisionTree.nodes.DTNode;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/26/15.
 */
public class DTCatBranchFinderBuilder extends BranchFinderBuilder<ClassificationCounter, DTNode> {

    @Override
    public DTCatBranchFinderBuilder createBranchFinderBuilder() {
        return new DTCatBranchFinderBuilder();
    }


    @Override
    public DTreeNClassCatBranchFinder buildBranchFinder(ClassificationCounter classificationCounts, Set<String> candidateAttributes) { //would be better if it took a configFacade, which i could extend have problem with number of ordinal splits.
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
        return new DTreeNClassCatBranchFinder(candidateAttributes, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }
}
