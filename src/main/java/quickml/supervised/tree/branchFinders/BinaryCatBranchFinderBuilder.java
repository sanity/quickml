package quickml.supervised.tree.branchFinders;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.BinaryClassAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.decisionTree.BinaryClassifierDataProperties;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.decisionTree.ClassifierDataProperties;
import static quickml.supervised.tree.constants.ForestOptions.*;
import java.util.Map;


/**
 * Created by alexanderhawk on 4/5/15.
 */

public class BinaryCatBranchFinderBuilder extends BranchFinderBuilder<ClassificationCounter, ClassifierDataProperties> {

    private BinaryClassAttributeValueIgnoringStrategyBuilder attributeValueIgnoringStrategyBuilder;

    @Override
    public BranchFinder<ClassificationCounter> buildBranchFinder(ClassifierDataProperties dataProperties) {
        AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.createAttributeValueIgnoringStrategy((BinaryClassifierDataProperties) dataProperties);
        return new SortableLabelsCategoricalBranchFinder<ClassificationCounter>(dataProperties.getCandidateAttributesOfAllBranchFinders().get(BranchType.CATEGORICAL), branchingConditions,
                scorer, attributeValueIgnoringStrategy,
                attributeIgnoringStrategy, branchType);
    }

    @Override
    public BranchFinderBuilder<ClassificationCounter, ClassifierDataProperties> createBranchFinderBuilder() {
        return new BinaryCatBranchFinderBuilder();
    }

    public void update(Map<String, Object> cfg) {
        super.update(cfg);
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER))
            attributeValueIgnoringStrategyBuilder = (BinaryClassAttributeValueIgnoringStrategyBuilder) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER);
    }

}


