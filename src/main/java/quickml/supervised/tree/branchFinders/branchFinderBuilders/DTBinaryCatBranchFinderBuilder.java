package quickml.supervised.tree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.DTBinaryCatBranchFinder;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.nodes.DTNode;

import java.util.Set;


/**
 * Created by alexanderhawk on 4/5/15.
 */

public class DTBinaryCatBranchFinderBuilder extends BranchFinderBuilder<ClassificationCounter, DTNode> {


    @Override
    public BranchFinder<ClassificationCounter, DTNode> buildBranchFinder(ClassificationCounter classificationCounts, Set<String> candidateAttributes) {
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
        return new DTBinaryCatBranchFinder(candidateAttributes, branchingConditions,
                scorer, attributeValueIgnoringStrategy,
                attributeIgnoringStrategy, branchType);
    }

    @Override
    public  BranchFinderBuilder<ClassificationCounter, DTNode> createBranchFinderBuilder() {
        return new DTBinaryCatBranchFinderBuilder();
    }

}


