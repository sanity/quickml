package quickml.supervised.tree.decisionTree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.SortableLabelsCategoricalBranchFinder;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.nodes.DTBranch;
import quickml.supervised.tree.decisionTree.nodes.DTCatBranch;
import quickml.supervised.tree.decisionTree.nodes.DTNode;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.*;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.reducers.AttributeStats;

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
