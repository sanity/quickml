package quickml.supervised.tree.nodes;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.decisionTree.BinaryClassAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.decisionTree.BinaryClassifierDataProperties;
import quickml.supervised.tree.ClassifierDataProperties;
import quickml.supervised.tree.decisionTree.tree.BranchType;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.BranchFinderBuilder;
import quickml.supervised.tree.branchFinders.SortableLabelsCategoricalBranchFinder;
import quickml.supervised.tree.branchFinders.TermStatsAndOperations;

import java.util.Map;

import static quickml.supervised.tree.decisionTree.tree.ForestOptions.ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER;

/**
 * Created by alexanderhawk on 4/5/15.
 */

//TODO: this is a horrible hack to make the generic type of this class a ClassifierDataProperties Object.  it should be a Binary ClassifierData properties object.
//make classifierDataProperties an interface, and add an isBinary method to it.
public class BinaryCatBranchFinderBuilder<TS extends TermStatsAndOperations<TS>> extends BranchFinderBuilder<TS, ClassifierDataProperties> {

    private BinaryClassAttributeValueIgnoringStrategyBuilder<TS> attributeValueIgnoringStrategyBuilder;

    @Override
    public BranchFinder<TS> buildBranchFinder(ClassifierDataProperties dataProperties) {
        AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy((BinaryClassifierDataProperties) dataProperties);
        return new SortableLabelsCategoricalBranchFinder<TS>(dataProperties.getCandidateAttributesOfAllBranchFinders().get(BranchType.CATEGORICAL), terminationConditions,
                scorer, attributeValueIgnoringStrategy,
                attributeIgnoringStrategy, branchType);
    }

    @Override
    public BranchFinderBuilder<TS, ClassifierDataProperties> createBranchFinderBuilder() {
        return new BinaryCatBranchFinderBuilder<TS>();
    }

    public void update(Map<String, Object> cfg) {
        super.update(cfg);
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER))
            attributeValueIgnoringStrategyBuilder = (BinaryClassAttributeValueIgnoringStrategyBuilder<TS>) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER);
    }

}


