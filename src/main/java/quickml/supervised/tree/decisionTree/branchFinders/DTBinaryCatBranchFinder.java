package quickml.supervised.tree.decisionTree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.SortableLabelsCategoricalBranchFinder;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.nodes.DTCatBranch;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.*;
import quickml.supervised.tree.scorers.GRImbalancedScorer;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.ScorerFactory;

import java.util.Set;

/**
 * Created by alexanderhawk on 6/11/15.
 */
public class DTBinaryCatBranchFinder extends SortableLabelsCategoricalBranchFinder<ClassificationCounter> {
    @Override
    public BranchType getBranchType() {
        return BranchType.BINARY_CATEGORICAL;
    }

    public DTBinaryCatBranchFinder(Set<String> candidateAttributes, BranchingConditions<ClassificationCounter> branchingConditions, ScorerFactory<ClassificationCounter> scorerFactory, AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        super(candidateAttributes, branchingConditions, scorerFactory, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }

    @Override
    protected Optional<? extends Branch<ClassificationCounter>> createBranch(Branch<ClassificationCounter> parent, AttributeStats<ClassificationCounter> attributeStats, SplittingUtils.SplitScore splitScore) {
        return Optional.of(new DTCatBranch(parent, attributeStats.getAttribute(), splitScore.trueSet,
                        splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                        attributeStats.getAggregateStats()));
    }
}
