package quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.DTBinaryCatBranchFinder;
import quickml.supervised.tree.decisionTree.branchFinders.DTNumBranchFinder;
import quickml.supervised.tree.decisionTree.nodes.DTNode;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/26/15.
 */
public class DTNumBranchFinderBuilder extends BranchFinderBuilder<ClassificationCounter, DTNode> {

    @Override
    public DTNumBranchFinderBuilder createBranchFinderBuilder() {
        return new DTNumBranchFinderBuilder();
    }


    @Override
    public DTNumBranchFinder buildBranchFinder(ClassificationCounter classificationCounts, Set<String> candidateAttributes) {
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
        return new DTNumBranchFinder(candidateAttributes, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }
}
