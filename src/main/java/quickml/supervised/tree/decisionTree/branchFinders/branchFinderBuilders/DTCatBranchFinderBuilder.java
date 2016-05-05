package quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.attributeValueIgnoringStrategies.MultiClassAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.DTNClassCatBranchFinder;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/26/15.
 */
public class DTCatBranchFinderBuilder extends DTBranchFinderBuilder {

    @Override
    public DTCatBranchFinderBuilder createBranchFinderBuilder() {
        return new DTCatBranchFinderBuilder();
    }

    @Override
    public DTNClassCatBranchFinder buildBranchFinder(ClassificationCounter classificationCounts, Set<String> candidateAttributes) {
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy;
        if (getAttributeValueIgnoringStrategyBuilder() == null) {
            MultiClassAttributeValueIgnoringStrategyBuilder multiClassAttributeValueIgnoringStrategyBuilder = new MultiClassAttributeValueIgnoringStrategyBuilder(getMinAttributeOccurences());
            attributeValueIgnoringStrategy = multiClassAttributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);

        } else {
            attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);
        }
        return new DTNClassCatBranchFinder(candidateAttributes, branchingConditions, scorerFactory, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.CATEGORICAL;
    }
}
