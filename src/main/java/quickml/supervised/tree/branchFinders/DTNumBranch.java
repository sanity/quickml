package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.DTNumBranch;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.terminationConditions.TerminationConditions;

import java.util.Collection;

/**
 * Created by alexanderhawk on 6/12/15.
 */
public class DTNumBranchFinder extends  NumericBranchFinder<ClassificationCounter> {
    public DTNumBranchFinder(Collection<String> candidateAttributes, TerminationConditions<ClassificationCounter> terminationConditions, Scorer<ClassificationCounter> scorer, AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    protected Optional<? extends Branch<ClassificationCounter>> createBranch(Branch<ClassificationCounter> parent, AttributeStats<ClassificationCounter> attributeStats, SplittingUtils.SplitScore splitScore, double bestThreshold) {
        return Optional.of(new DTNumBranch(parent, attributeStats.getAttribute(),
                splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                attributeStats.getAggregateStats(), bestThreshold));
    }
}
