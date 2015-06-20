package quickml.supervised.tree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.DTBinaryCatBranchFinder;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.nodes.DTNode;

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
    public DTBinaryCatBranchFinder buildBranchFinder(ClassificationCounter classificationCounts, Set<String> candidateAttributes) { //would be better if it took a configFacade, which i could extend have problem with number of ordinal splits.
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
        return new DTBinaryCatBranchFinder(candidateAttributes, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }
}
