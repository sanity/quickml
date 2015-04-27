package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.base.Optional;
import org.javatuples.Pair;
import quickml.supervised.classifier.AttributeValueIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.scorers.ScorerUtils;
import quickml.supervised.classifier.tree.decisionTree.tree.*;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.AttributeStats;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.NumericBranch;

import java.util.Collection;
import java.util.List;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class NumericBranchFinder<TS extends TermStatsAndOperations<TS>> extends BranchFinder<TS> {
    public NumericBranchFinder(Collection<String> candidateAttributes, TerminationConditions<TS> terminationConditions, Scorer<TS> scorer, AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    public Optional<? extends Branch<TS>> getBranch(Branch parent, AttributeStats<TS> attributeStats) {
        if (attributeStats.getTermStats().size()<=1) {
            return Optional.absent();
        }

        SplittingUtils.SplitScore splitScore = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, terminationConditions, attributeValueIgnoringStrategy);
        if (splitScore.score <= terminationConditions.getMinScore()) {
           Optional.absent();
        }
        double bestThreshold = (Double)attributeStats.getTermStats().get(splitScore.indexOfLastTermStatsInTrueSet).getAttrVal();
        return Optional.of(new NumericBranch<TS>(parent, attributeStats.getAttribute(),
                splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                attributeStats.getAggregateStats(), bestThreshold));
    }
}
