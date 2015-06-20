package quickml.supervised.tree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.terminationConditions.BranchingConditions;
import static quickml.supervised.tree.constants.ForestOptions.*;

import java.util.Map;
import java.util.Set;


/**
 * Created by alexanderhawk on 3/19/15.
 */
public abstract class BranchFinderBuilder<VC extends ValueCounter<VC>, N extends Node<VC, N>> {
    protected BranchingConditions<VC, N> branchingConditions;
    protected Scorer<VC> scorer;
    protected AttributeIgnoringStrategy attributeIgnoringStrategy;
    protected AttributeValueIgnoringStrategyBuilder<VC> attributeValueIgnoringStrategyBuilder;
    protected BranchType branchType;

    public BranchType getBranchType() {
        return branchType;
    }

    public AttributeIgnoringStrategy getAttributeIgnoringStrategy() {
        return attributeIgnoringStrategy;
    }

    public void setBranchingConditions(BranchingConditions<VC, N> branchingConditions) {
        this.branchingConditions = branchingConditions;
    }

    public void setScorer(Scorer<VC> scorer) {
        this.scorer = scorer;
    }


    public void setAttributeValueIgnoringStrategyBuilder(AttributeValueIgnoringStrategyBuilder<VC> attributeValueIgnoringStrategyBuilder) {
        this.attributeValueIgnoringStrategyBuilder = attributeValueIgnoringStrategyBuilder;
    }

    public void setAttributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
    }

    public void update(Map<String, Object> cfg) {
        if (cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY.name()))
            attributeIgnoringStrategy= (AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_IGNORING_STRATEGY.name());
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name()))
            attributeValueIgnoringStrategyBuilder= (AttributeValueIgnoringStrategyBuilder<VC>) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name());
        if (cfg.containsKey(SCORER.name()))
            scorer = (Scorer<VC>) cfg.get(SCORER.name());
        if (cfg.containsKey(TERMINATION_CONDITIONS.name()))
            branchingConditions = (BranchingConditions<VC, N>) cfg.get(TERMINATION_CONDITIONS.name());
    }

    public BranchFinderBuilder<VC, N> copy() {
        BranchFinderBuilder<VC, N> copy = createBranchFinderBuilder();
        copy.branchingConditions = branchingConditions.copy();
        copy.setScorer(scorer);
        copy.setAttributeIgnoringStrategy(attributeIgnoringStrategy.copy());
        copy.setAttributeValueIgnoringStrategyBuilder(attributeValueIgnoringStrategyBuilder.copy());
        copy.branchType = branchType;
        return copy;
    }

    public abstract BranchFinderBuilder<VC, N> createBranchFinderBuilder();

    public abstract BranchFinder<VC, N> buildBranchFinder(VC valueCounter, Set<String> candidateAttributes);


}

