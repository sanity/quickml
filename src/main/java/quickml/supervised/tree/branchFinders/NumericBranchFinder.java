package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Node;
import quickml.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.branchingConditions.BranchingConditions;

import java.util.Collection;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public abstract class NumericBranchFinder<VC extends ValueCounter<VC>> extends BranchFinder<VC> {
    public NumericBranchFinder(Collection<String> candidateAttributes, BranchingConditions<VC> branchingConditions, Scorer<VC> scorer, AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        super(candidateAttributes, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }

    @Override
    public Optional<? extends Branch<VC>> getBranch(Branch<VC> parent, AttributeStats<VC> attributeStats) {
        if (attributeStats.getStatsOnEachValue().size()<=1) {
            return Optional.absent();
        }

        Optional<SplittingUtils.SplitScore> splitScoreOptional = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, branchingConditions, attributeValueIgnoringStrategy, false);
        if (!splitScoreOptional.isPresent()) {
           return Optional.absent();
        }
        SplittingUtils.SplitScore splitScore = splitScoreOptional.get();
        double bestThreshold = (Double)attributeStats.getStatsOnEachValue().get(splitScore.indexOfLastTermStatsInTrueSet).getAttrVal();
        return createBranch(parent, attributeStats, splitScore, bestThreshold);
    }
    protected abstract Optional<? extends Branch<VC>> createBranch(Branch<VC> parent, AttributeStats<VC> attributeStats, SplittingUtils.SplitScore splitScore, double bestThreshold);

}
