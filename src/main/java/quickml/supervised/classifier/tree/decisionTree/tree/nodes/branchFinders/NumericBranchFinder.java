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

import java.util.List;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class NumericBranchFinder extends BranchFinder<ClassificationCounter> {
    public NumericBranchFinder(List<String> candidateAttributes, TerminationConditions<ClassificationCounter> terminationConditions, Scorer<ClassificationCounter> scorer, AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    public Optional<? extends Branch<ClassificationCounter>> getBranch(Branch parent, AttributeStats<ClassificationCounter> attributeStats) {
        if (attributeStats.getTermStats().size()<=1) {
            return Optional.absent();
        }

        SplittingUtils.SplitScore splitScore = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, terminationConditions, attributeValueIgnoringStrategy);
        if (splitScore.score <= terminationConditions.getMinScore()) {
           Optional.absent();
        }
        double bestThreshold = (Double)attributeStats.getTermStats().get(splitScore.indexOfLastTermStatsInTrueSet).getAttrVal();
        return Optional.of(new NumericBranch(parent, attributeStats.getAttribute(),
                splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                attributeStats.getAggregateStats(), bestThreshold));
    }
}
