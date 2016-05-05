package quickml.supervised.tree.decisionTree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.NumericBranchFinder;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.decisionTree.nodes.DTNumBranch;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.scorers.ScorerFactory;

import java.util.Collection;

/**
 * Created by alexanderhawk on 6/12/15.
 */
public class DTNumBranchFinder extends NumericBranchFinder<ClassificationCounter> {
    public DTNumBranchFinder(Collection<String> candidateAttributes, BranchingConditions<ClassificationCounter> branchingConditions, ScorerFactory<ClassificationCounter> scorerFactory, AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        super(candidateAttributes, branchingConditions, scorerFactory, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.NUMERIC;
    }


    @Override
    protected Optional<? extends Branch<ClassificationCounter>> createBranch(Branch<ClassificationCounter> parent, AttributeStats<ClassificationCounter> attributeStats, SplittingUtils.SplitScore splitScore, double bestThreshold) {
        return Optional.of(new DTNumBranch(parent, attributeStats.getAttribute(),
                splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                attributeStats.getAggregateStats(), bestThreshold));
    }
}
