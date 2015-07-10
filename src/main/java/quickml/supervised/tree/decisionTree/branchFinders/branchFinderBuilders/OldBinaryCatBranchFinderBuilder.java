package quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.attributeValueIgnoringStrategies.BinaryClassAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.OldBinCatBranchFinder;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.util.Set;


/**
 * Created by alexanderhawk on 4/5/15.
 */

public class OldBinaryCatBranchFinderBuilder extends DTBranchFinderBuilder {
    private static final long serialVersionUID = 0L;

    @Override
    public  BranchFinderBuilder<ClassificationCounter> createBranchFinderBuilder() {
        return new OldBinaryCatBranchFinderBuilder();
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.BINARY_CATEGORICAL;
    }

    @Override
    public BranchFinder<ClassificationCounter> buildBranchFinder(ClassificationCounter classificationCounts, Set<String> candidateAttributes) {
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy;
        if(attributeValueIgnoringStrategyBuilder==null) {
            BinaryClassAttributeValueIgnoringStrategyBuilder binaryClassAttributeValueIgnoringStrategyBuilder = new BinaryClassAttributeValueIgnoringStrategyBuilder(getMinAttributeOccurences());
            attributeValueIgnoringStrategy = binaryClassAttributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);

        } else {
            attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy(classificationCounts);

        }
        return new OldBinCatBranchFinder(candidateAttributes, branchingConditions,
                scorerFactory, attributeValueIgnoringStrategy,
                attributeIgnoringStrategy);
    }


}


