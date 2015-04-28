package quickml.supervised.tree.decisionTree;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.CategoricalBranch;

import java.util.*;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class multiClassCategoricalBranchFinder<TS extends TermStatsAndOperations<TS>> extends BranchFinder<TS> {
    public multiClassCategoricalBranchFinder(List<String> candidateAttributes, TerminationConditions<TS> terminationConditions, Scorer<TS> scorer, AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, BranchType branchType) {
        super(candidateAttributes, terminationConditions, scorer, attributeValueIgnoringStrategy, attributeIgnoringStrategy, branchType);
    }

    @Override
    public Optional<? extends Branch<TS>> getBranch(Branch parent, AttributeStats<TS> attributeStats) {

        Set<TS> termStatsSet = Sets.newHashSet(attributeStats.getTermStats());
        if (termStatsSet.size() <= 1) {
            return Optional.absent();
        }

        double insetScore = 0;
        TS falseSet = attributeStats.getAggregateStats();
        TS trueSet = falseSet.subtract(falseSet); //emptySet

        scorer.setIntrinsicValue(attributeStats);
        scorer.setUnSplitScore(attributeStats.getAggregateStats());
        Set<Object> trueValSet = Sets.newHashSet();
        while (true) {
            // addNextBestAttrValToTrueSet
            Optional<ScoreValuePair<TS>> scoreValuePair = getTermStatsOfNextBestAttributeVal(termStatsSet, falseSet, trueSet);

            if (scoreValuePair.isPresent() && scoreValuePair.get().getScore() > insetScore) {
                insetScore = scoreValuePair.get().getScore();
                TS termStats = scoreValuePair.get().getTermStats();
                trueValSet.add(termStats.getAttrVal());
                termStatsSet.remove(termStats);
                trueSet = trueSet.add(termStats);
                falseSet = falseSet.subtract(termStats);

            } else {
                break;
            }
        }
        double probabilityOfBeingInInset = trueSet.getTotal() / (trueSet.getTotal() + falseSet.getTotal());

        if (trueValSet.isEmpty()) {
            return Optional.absent();
        }

        return Optional.of(new CategoricalBranch(parent, parent.attribute, trueValSet, probabilityOfBeingInInset, insetScore, attributeStats.getAggregateStats()));
    }

    private Optional<ScoreValuePair<TS>> getTermStatsOfNextBestAttributeVal(Collection<TS> termStatsList, TS falseSet, TS trueSet) {
        double bestScore = 0.0;
        TS termStatsOfBestVal = null;
        Iterator<TS> tsIterator = termStatsList.iterator();

        while (tsIterator.hasNext()) {
            TS termStats = tsIterator.next();
            if (termStats.getAttrVal().equals(MissingValue.MISSING_VALUE) || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(termStats)) {
                continue;
            }

            final TS testTrueSet = trueSet.add(termStats);
            final TS testFalseSet = falseSet.subtract(termStats);

            if (terminationConditions.isInvalidSplit(testTrueSet, testFalseSet)) {
                continue;
            }

            double score = scorer.scoreSplit(testTrueSet, testFalseSet);

            if (score > bestScore) {
                termStatsOfBestVal = termStats;
            }
        }
        if (termStatsOfBestVal ==null) {
            return Optional.absent();
        }

        return Optional.of(new ScoreValuePair<TS>(bestScore, termStatsOfBestVal));
    }

    static class ScoreValuePair<TS extends TermStatsAndOperations<TS>> {
        public double getScore() {
            return score;
        }

        public TS getTermStats() {
            return termStats;
        }

        double score;
        TS termStats;

        public ScoreValuePair(double score, TS termStats) {
            this.score = score;
            this.termStats = termStats;
        }
    }


}
