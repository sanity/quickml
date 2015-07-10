package quickml.supervised.tree.decisionTree.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.constants.MissingValue;
import quickml.supervised.tree.decisionTree.nodes.DTCatBranch;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.*;
import quickml.supervised.tree.scorers.GRImbalancedScorer;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.scorers.ScorerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public class DTNClassCatBranchFinder extends BranchFinder<ClassificationCounter> {

    public DTNClassCatBranchFinder(Collection<String> candidateAttributes, BranchingConditions<ClassificationCounter> branchingConditions, ScorerFactory<ClassificationCounter> scorerFactory, AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy) {
        super(candidateAttributes, branchingConditions, scorerFactory, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
    }

    @Override
    public BranchType getBranchType() {
        return BranchType.CATEGORICAL;
    }


    @Override
    public Optional<? extends Branch<ClassificationCounter>> getBranch(Branch<ClassificationCounter> parent,
                                                                               AttributeStats<ClassificationCounter> attributeStats) {

        final Set<Serializable> trueSet = Sets.newHashSet();
        ClassificationCounter trueClassificationCounts = new ClassificationCounter();
        ClassificationCounter falseClassificationCounts = attributeStats.getAggregateStats();
        final List<ClassificationCounter> valueOutcomeCounts = attributeStats.getStatsOnEachValue();
        Map<Serializable, ClassificationCounter> attrValToCCMap = Maps.newHashMap();
        for (ClassificationCounter classificationCounter: valueOutcomeCounts) {
            attrValToCCMap.put(classificationCounter.getAttrVal(), classificationCounter);
        }

        Scorer<ClassificationCounter> scorer = scorerFactory.getScorer(attributeStats);
        double scoreWithCurrentTrueSet = 0;
        while (true) {
            Optional<ScoreValuePair> bestValueAndScore = getNextBestAttributeValueToAddToTrueSet(trueClassificationCounts, falseClassificationCounts, attrValToCCMap, scorer);

            if (bestValueAndScore.isPresent() && bestValueAndScore.get().getScore() > scoreWithCurrentTrueSet) {
                scoreWithCurrentTrueSet = bestValueAndScore.get().getScore();
                final Serializable bestValue = bestValueAndScore.get().getValue();
                trueSet.add(bestValue);
                final ClassificationCounter bestValOutcomeCounts = attrValToCCMap.get(bestValue);
                trueClassificationCounts = trueClassificationCounts.add(bestValOutcomeCounts);
                falseClassificationCounts = falseClassificationCounts.subtract(bestValOutcomeCounts);
                attrValToCCMap.remove(bestValue);
            } else {
                break;
            }
        }
        if (branchingConditions.isInvalidSplit(trueClassificationCounts, falseClassificationCounts, attributeStats.getAttribute()) || branchingConditions.isInvalidSplit(scoreWithCurrentTrueSet)) {
            return Optional.absent();
        }
        //because trueClassificationCounts is only mutated to better insets during the for loop...it corresponds to the actual inset here.
        double probabilityOfBeingInTrueSet = trueClassificationCounts.getTotal() / (trueClassificationCounts.getTotal() + falseClassificationCounts.getTotal());
        return Optional.of(new DTCatBranch(parent, attributeStats.getAttribute(), trueSet, probabilityOfBeingInTrueSet, scoreWithCurrentTrueSet,attributeStats.getAggregateStats()));
    }

    private Optional<ScoreValuePair> getNextBestAttributeValueToAddToTrueSet(ClassificationCounter trueClassificationCounts, ClassificationCounter falseClassificationCounts, Map<Serializable, ClassificationCounter> attrValToCCMap, Scorer<ClassificationCounter> scorer) {
        Optional<ScoreValuePair> bestValueAndScore = Optional.absent();
        //values should be greater than 1

        for (final Serializable attrVal : attrValToCCMap.keySet()) {
            ClassificationCounter cc = attrValToCCMap.get(attrVal);
            if (    attrVal== null
                    || attrVal.equals(MissingValue.MISSING_VALUE)
                    || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(cc)) {
                continue;
            }

            final ClassificationCounter testInCounts = trueClassificationCounts.add(cc);
            final ClassificationCounter testOutCounts = falseClassificationCounts.subtract(cc);

            double scoreWithThisValueAddedToTrueSet = scorer.scoreSplit(testInCounts, testOutCounts);

            if (!bestValueAndScore.isPresent() || scoreWithThisValueAddedToTrueSet > bestValueAndScore.get().getScore()) {
                bestValueAndScore = Optional.of(new ScoreValuePair(scoreWithThisValueAddedToTrueSet, attrVal));
            }
        }
        return bestValueAndScore;
    }

    static class ScoreValuePair {
        double score;
        Serializable value;

        public ScoreValuePair(double score, Serializable value) {
            this.score = score;
            this.value = value;
        }

        public double getScore() {
            return score;
        }

        public Serializable getValue() {
            return value;
        }
    }


}

