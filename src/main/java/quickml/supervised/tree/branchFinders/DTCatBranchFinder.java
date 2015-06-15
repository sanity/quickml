package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.DTCatBranch;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.terminationConditions.TerminationConditions;

import java.util.Set;

/**
 * Created by alexanderhawk on 6/11/15.
 */
public class DTCatBranchFinder extends SortableLabelsCategoricalBranchFinder<ClassificationCounter> {

    public DTCatBranchFinder(Set<String> candidateAttributes, TerminationConditions<ClassificationCounter> terminationConditions, Scorer<ClassificationCounter> scorer, AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    protected Optional<? extends Branch<ClassificationCounter>> createBranch(Branch<ClassificationCounter> parent, AttributeStats<ClassificationCounter> attributeStats, SplittingUtils.SplitScore splitScore) {
        return Optional.of(new DTCatBranch((DTCatBranch) parent, attributeStats.getAttribute(), splitScore.trueSet,
                        splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                        attributeStats.getAggregateStats()));
    }
}
