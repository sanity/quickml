package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.DTNumBranch;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.NumBranch;
import quickml.supervised.tree.terminationConditions.TerminationConditions;

import java.util.Collection;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public abstract class NumericBranchFinder<TS extends TermStatsAndOperations<TS>> extends BranchFinder<TS> {
    public NumericBranchFinder(Collection<String> candidateAttributes, TerminationConditions<TS> terminationConditions, Scorer<TS> scorer, AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    public Optional<? extends Branch<TS>> getBranch(Branch<TS> parent, AttributeStats<TS> attributeStats) {
        if (attributeStats.getTermStats().size()<=1) {
            return Optional.absent();
        }

        SplittingUtils.SplitScore splitScore = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, terminationConditions, attributeValueIgnoringStrategy);
        if (splitScore.score <= terminationConditions.getMinScore()) {
           Optional.absent();
        }
        double bestThreshold = (Double)attributeStats.getTermStats().get(splitScore.indexOfLastTermStatsInTrueSet).getAttrVal();
        return createBranch(parent, attributeStats, splitScore, bestThreshold);
    }
    protected abstract Optional<? extends Branch<TS>> createBranch(Branch<TS> parent, AttributeStats<TS> attributeStats, SplittingUtils.SplitScore splitScore, double bestThreshold);

}
