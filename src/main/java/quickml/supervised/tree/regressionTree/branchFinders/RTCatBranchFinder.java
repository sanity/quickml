package quickml.supervised.tree.regressionTree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.SortableLabelsCategoricalBranchFinder;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.*;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.regressionTree.nodes.RTCatBranch;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;
import quickml.supervised.tree.scorers.ScorerFactory;

import java.util.Set;

/**
 * Created by alexanderhawk on 6/11/15.
 */
public class RTCatBranchFinder extends SortableLabelsCategoricalBranchFinder<MeanValueCounter> {
    @Override
    public BranchType getBranchType() {
        return BranchType.RT_CATEGORICAL;
    }

    public RTCatBranchFinder(Set<String> candidateAttributes, BranchingConditions<MeanValueCounter> branchingConditions, ScorerFactory<MeanValueCounter> scorerFactory, AttributeValueIgnoringStrategy<MeanValueCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        super(candidateAttributes, branchingConditions, scorerFactory, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }

    @Override
    protected Optional<? extends Branch<MeanValueCounter>> createBranch(Branch<MeanValueCounter> parent, AttributeStats<MeanValueCounter> attributeStats, SplittingUtils.SplitScore splitScore) {
        return Optional.of(new RTCatBranch(parent, attributeStats.getAttribute(), splitScore.trueSet,
                        splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                        attributeStats.getAggregateStats()));
    }
}
