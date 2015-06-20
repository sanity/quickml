package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.nodes.*;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.terminationConditions.BranchingConditions;

import java.util.Set;

/**
 * Created by alexanderhawk on 6/11/15.
 */
public class DTBinaryCatBranchFinder extends SortableLabelsCategoricalBranchFinder<ClassificationCounter, DTNode> {

    public DTBinaryCatBranchFinder(Set<String> candidateAttributes, BranchingConditions<ClassificationCounter, DTNode> branchingConditions, Scorer<ClassificationCounter> scorer, AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, branchingConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    protected Optional<? extends Branch<ClassificationCounter, DTNode>> createBranch(Branch<ClassificationCounter, DTNode> parent, AttributeStats<ClassificationCounter> attributeStats, SplittingUtils.SplitScore splitScore) {
        return Optional.of(new DTCatBranch((DTBranch) parent, attributeStats.getAttribute(), splitScore.trueSet,
                        splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                        attributeStats.getAggregateStats()));
    }
}
