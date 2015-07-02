package quickml.supervised.tree.branchFinders.branchFinderBuilders;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import static quickml.supervised.tree.constants.ForestOptions.*;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;


/**
 * Created by alexanderhawk on 3/19/15.
 */
public abstract class BranchFinderBuilder<VC extends ValueCounter<VC>> implements Serializable{
    private static final long serialVersionUID = 0L;

    protected BranchingConditions<VC> branchingConditions;
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

    public BranchingConditions<VC> getBranchingConditions() {
        return branchingConditions;
    }

    public abstract BranchType getBranchType();

    public AttributeIgnoringStrategy getAttributeIgnoringStrategy() {
        return attributeIgnoringStrategy;
    }

    public void update(Map<String, Serializable> cfg) {
        if (cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY.name()))
            attributeIgnoringStrategy= (AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_IGNORING_STRATEGY.name());
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name()))
            attributeValueIgnoringStrategyBuilder= (AttributeValueIgnoringStrategyBuilder<VC>) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name());
        if (cfg.containsKey(SCORER.name()))
            scorer = (Scorer<VC>) cfg.get(SCORER.name());
        if (cfg.containsKey(BRANCHING_CONDITIONS.name()))
            branchingConditions = (BranchingConditions<VC>) cfg.get(BRANCHING_CONDITIONS.name());
        if (cfg.containsKey(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name()))
            minAttributeOccurences = (Integer)cfg.get(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name());
    }


    public  BranchFinderBuilder<VC> copy() {
        BranchFinderBuilder<VC> copy = createBranchFinderBuilder();
        copy.branchType = branchType;
        if (branchingConditions!=null) {
            copy.branchingConditions = branchingConditions.copy();
        }
        if (scorer!=null) {
            copy.scorer = scorer.copy();
        }
        if (attributeIgnoringStrategy!=null) {
            copy.attributeIgnoringStrategy = attributeIgnoringStrategy.copy();
        }
        if (attributeValueIgnoringStrategyBuilder!= null) {
            copy.attributeValueIgnoringStrategyBuilder = attributeValueIgnoringStrategyBuilder.copy();
        }
        return copy;
    }

    public abstract BranchFinderBuilder<VC> createBranchFinderBuilder();

    public abstract BranchFinder<VC> buildBranchFinder(VC valueCounter, Set<String> candidateAttributes);


}

