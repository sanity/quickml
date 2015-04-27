package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import quickml.supervised.classifier.AttributeValueIgnoringStrategy;
import quickml.supervised.classifier.BinaryClassAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.*;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static quickml.supervised.classifier.tree.decisionTree.tree.ForestOptions.*;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public abstract class BranchFinderBuilder<TS extends TermStatsAndOperations<TS>, D extends DataProperties> {
    protected TerminationConditions<TS> terminationConditions;
    protected Scorer<TS> scorer;
    protected AttributeIgnoringStrategy attributeIgnoringStrategy;
    protected AttributeValueIgnoringStrategyBuilder<TS, D> attributeValueIgnoringStrategyBuilder;
    protected BranchType branchType;

    public BranchType getBranchType() {
        return branchType;
    }

    public AttributeIgnoringStrategy getAttributeIgnoringStrategy() {
        return attributeIgnoringStrategy;
    }

    public void setTerminationConditions(TerminationConditions<TS> terminationConditions) {
        this.terminationConditions = terminationConditions;
    }

    public void setScorer(Scorer<TS> scorer) {
        this.scorer = scorer;
    }


    public void setAttributeValueIgnoringStrategyBuilder(AttributeValueIgnoringStrategyBuilder<TS, D> attributeValueIgnoringStrategyBuilder) {
        this.attributeValueIgnoringStrategyBuilder = attributeValueIgnoringStrategyBuilder;
    }

    public void setAttributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
    }

    public void update(Map<String, Object> cfg) {
        if (cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY))
            attributeIgnoringStrategy= (AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_IGNORING_STRATEGY);
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER))
            attributeValueIgnoringStrategyBuilder= (AttributeValueIgnoringStrategyBuilder<TS, D>) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER);
        if (cfg.containsKey(SCORER))
            scorer = (Scorer<TS>) cfg.get(SCORER);
        if (cfg.containsKey(TERMINATION_CONDITIONS))
            terminationConditions = (TerminationConditions<TS>) cfg.get(TERMINATION_CONDITIONS);
    }

    public BranchFinderBuilder<TS, D> copy() {
        BranchFinderBuilder<TS,D> copy = createBranchFinderBuilder();
        copy.terminationConditions = terminationConditions.copy();
        copy.setScorer(scorer);
        copy.setAttributeIgnoringStrategy(attributeIgnoringStrategy.copy());
        copy.setAttributeValueIgnoringStrategyBuilder(attributeValueIgnoringStrategyBuilder.copy());
        copy.branchType = branchType;
        return copy;
    }

    public abstract BranchFinderBuilder<TS, D> createBranchFinderBuilder();

    public abstract BranchFinder<TS> buildBranchFinder(D dataProperties);


}

