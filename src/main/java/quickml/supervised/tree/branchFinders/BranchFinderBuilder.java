package quickml.supervised.tree.branchFinders;

import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.terminationConditions.TerminationConditions;
import static quickml.supervised.tree.constants.ForestOptions.*;

import java.util.Map;


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
        if (cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY.name()))
            attributeIgnoringStrategy= (AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_IGNORING_STRATEGY.name());
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name()))
            attributeValueIgnoringStrategyBuilder= (AttributeValueIgnoringStrategyBuilder<TS, D>) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name());
        if (cfg.containsKey(SCORER.name()))
            scorer = (Scorer<TS>) cfg.get(SCORER.name());
        if (cfg.containsKey(TERMINATION_CONDITIONS.name()))
            terminationConditions = (TerminationConditions<TS>) cfg.get(TERMINATION_CONDITIONS.name());
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

