package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.CategoricalBranch;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class SortableLabelsCategoricalBranchFinder<TS extends TermStatsAndOperations<TS>> extends BranchFinder<TS> {

    public SortableLabelsCategoricalBranchFinder(Set<String> candidateAttributes, TerminationConditions<TS> terminationConditions,
                                                 Scorer<TS> scorer, AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy,
                                                 AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    public Optional<? extends Branch<TS>> getBranch(Branch parent, AttributeStats<TS> attributeStats) {
        //assumption: attributeStats has sorted vals
        if (attributeStats.getTermStats().size()<=1) {
            return Optional.absent();
        }

        SplittingUtils.SplitScore splitScore = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, terminationConditions, attributeValueIgnoringStrategy);
        if (splitScore.score <= terminationConditions.getMinScore()) {
            Optional.absent();
        }
        return Optional.of(new CategoricalBranch(parent, attributeStats.getAttribute(), splitScore.trueSet,
                splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                attributeStats.getAggregateStats()));
    }
}
