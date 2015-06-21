package quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.DTBinaryCatBranchFinder;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.decisionTree.nodes.DTNode;

import java.util.Set;


/**
 * Created by alexanderhawk on 4/5/15.
 */

public class DTBinaryCatBranchFinderBuilder extends BranchFinderBuilder<ClassificationCounter, DTNode> {


    @Override
    public  BranchFinderBuilder<ClassificationCounter, DTNode> createBranchFinderBuilder() {
        return new DTBinaryCatBranchFinderBuilder();
    }

    @Override
    public BranchFinder<ClassificationCounter, DTNode> buildBranchFinder(ClassificationCounter classificationCounts, Set<String> candidateAttributes) {
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
        return new DTBinaryCatBranchFinder(candidateAttributes, branchingConditions,
                scorer, attributeValueIgnoringStrategy,
                attributeIgnoringStrategy, branchType);
    }

}


