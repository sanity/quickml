package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.AttributeValueIgnoringStrategy;
import quickml.supervised.classifier.BinaryClassAttributeValueIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.scorers.ScorerUtils;
import quickml.supervised.classifier.tree.decisionTree.tree.*;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.AttributeStats;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.CategoricalBranch;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.NumericBranch;

import static quickml.supervised.classifier.tree.decisionTree.tree.MissingValue.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class SortableLabelsCategoricalBranchFinder<TS extends TermStatsAndOperations<TS>> extends BranchFinder<TS> {

    public SortableLabelsCategoricalBranchFinder(List<String> candidateAttributes, TerminationConditions<TS> terminationConditions,
                                                 Scorer<TS> scorer, AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy,
                                                 AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    public Optional<? extends Branch<TS>> getBranch(Branch parent, AttributeStats<TS> attributeStats) {
        //assumption: attributeStats has sorted vals
        if (attributeStats.getTermStats().size()<=1) {
            return Optional.absent();
        }

        SplittingUtils.SplitScore splitScore = SplittingUtils.splitSortedAttributeStats(attributeStats, scorer, terminationConditions, attributeValueIgnoringStrategy);
        if (splitScore.score <= terminationConditions.getMinScore()) {
            Optional.absent();
        }
        return Optional.of(new CategoricalBranch(parent, attributeStats.getAttribute(), splitScore.trueSet,
                splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                attributeStats.getAggregateStats()));
    }
}
