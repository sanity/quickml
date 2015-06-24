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

import java.util.Set;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public abstract class SortableLabelsCategoricalBranchFinder<VC extends ValueCounter<VC>> extends BranchFinder<VC> {

    public SortableLabelsCategoricalBranchFinder(Set<String> candidateAttributes, BranchingConditions<VC> branchingConditions,
                                                 Scorer<VC> scorer, AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy,
                                                 AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }



    @Override
    public Optional<? extends Branch<VC>> getBranch(Branch<VC> parent, AttributeStats<VC> attributeStats) {
        if (attributeStats.getStatsOnEachValue().size()<=1) {
            return Optional.absent();
        }

        SplittingUtils.SplitScore splitScore = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, branchingConditions, attributeValueIgnoringStrategy);
        if (branchingConditions.isInvalidSplit(splitScore.score)) {
            Optional.absent();
        }
        return createBranch(parent, attributeStats, splitScore);

    }
    protected abstract Optional<? extends Branch<VC>> createBranch(Branch<VC> parent, AttributeStats<VC> attributeStats, SplittingUtils.SplitScore splitScore);
}
