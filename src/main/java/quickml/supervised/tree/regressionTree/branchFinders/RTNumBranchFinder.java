package quickml.supervised.tree.regressionTree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.NumericBranchFinder;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.regressionTree.nodes.RTNumBranch;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;
import quickml.supervised.tree.scorers.ScorerFactory;

import java.util.Collection;

/**
 * Created by alexanderhawk on 6/12/15.
 */
public class RTNumBranchFinder extends NumericBranchFinder<MeanValueCounter> {
    public RTNumBranchFinder(Collection<String> candidateAttributes, BranchingConditions<MeanValueCounter> branchingConditions, ScorerFactory<MeanValueCounter> scorerFactory, AttributeValueIgnoringStrategy<MeanValueCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        super(candidateAttributes, branchingConditions, scorerFactory, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.RT_NUMERIC;
    }


    @Override
    protected Optional<? extends Branch<MeanValueCounter>> createBranch(Branch<MeanValueCounter> parent, AttributeStats<MeanValueCounter> attributeStats, SplittingUtils.SplitScore splitScore, double bestThreshold) {
        return Optional.of(new RTNumBranch(parent, attributeStats.getAttribute(),
                splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                attributeStats.getAggregateStats(), bestThreshold));
    }
}
