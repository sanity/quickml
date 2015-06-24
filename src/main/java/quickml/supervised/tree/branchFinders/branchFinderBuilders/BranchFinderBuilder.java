package quickml.supervised.tree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Node;
import quickml.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
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
    protected int minAttributeOccurences = 0;

    public int getMinAttributeOccurences() {
        return minAttributeOccurences;
    }

    public AttributeValueIgnoringStrategyBuilder<VC> getAttributeValueIgnoringStrategyBuilder() {
        return attributeValueIgnoringStrategyBuilder;
    }

    public Scorer<VC> getScorer() {
        return scorer;
    }

    public BranchingConditions<VC, N> getBranchingConditions() {
        return branchingConditions;
    }

    public BranchType getBranchType() {
        return branchType;
    }

    public AttributeIgnoringStrategy getAttributeIgnoringStrategy() {
        return attributeIgnoringStrategy;
    }

    public void update(Map<String, Object> cfg) {
        if (cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY.name()))
            attributeIgnoringStrategy= (AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_IGNORING_STRATEGY.name());
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name()))
            attributeValueIgnoringStrategyBuilder= (AttributeValueIgnoringStrategyBuilder<VC>) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name());
        if (cfg.containsKey(SCORER.name()))
            scorer = (Scorer<VC>) cfg.get(SCORER.name());
        if (cfg.containsKey(BRANCHING_CONDITIONS.name()))
            branchingConditions = (BranchingConditions<VC, N>) cfg.get(BRANCHING_CONDITIONS.name());
        if (cfg.containsKey(MIN_ATTRIBUTE_OCCURRENCES.name()))
            minAttributeOccurences = (Integer)cfg.get(MIN_ATTRIBUTE_OCCURRENCES.name());
    }

    public BranchFinderBuilder<VC, N> copy() {
        BranchFinderBuilder<VC, N> copy = createBranchFinderBuilder();
        copy.branchingConditions = branchingConditions.copy();
        copy.scorer = scorer.copy();
        copy.attributeIgnoringStrategy = attributeIgnoringStrategy.copy();
        copy.attributeValueIgnoringStrategyBuilder = attributeValueIgnoringStrategyBuilder.copy();
        copy.branchType = branchType;
        return copy;
    }

    public abstract BranchFinderBuilder<VC, N> createBranchFinderBuilder();

    public abstract BranchFinder<VC, N> buildBranchFinder(VC valueCounter, Set<String> candidateAttributes);


}

