package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.terminationConditions.BranchingConditions;

import java.util.Collection;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public abstract class NumericBranchFinder<VC extends ValueCounter<VC>, N extends Node<VC, N>> extends BranchFinder<VC, N> {
    public NumericBranchFinder(Collection<String> candidateAttributes, BranchingConditions<VC> branchingConditions, Scorer<VC> scorer, AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    public Optional<? extends Branch<VC, N>> getBranch(Branch<VC, N> parent, AttributeStats<VC> attributeStats) {
        if (attributeStats.getTermStats().size()<=1) {
            return Optional.absent();
        }

        SplittingUtils.SplitScore splitScore = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, branchingConditions, attributeValueIgnoringStrategy);
        if (branchingConditions.isInvalidSplit(splitScore.score)) {
           return Optional.absent();
        }
        double bestThreshold = (Double)attributeStats.getTermStats().get(splitScore.indexOfLastTermStatsInTrueSet).getAttrVal();
        return createBranch(parent, attributeStats, splitScore, bestThreshold);
    }
    protected abstract Optional<? extends Branch<VC, N>> createBranch(Branch<VC, N> parent, AttributeStats<VC> attributeStats, SplittingUtils.SplitScore splitScore, double bestThreshold);

}
