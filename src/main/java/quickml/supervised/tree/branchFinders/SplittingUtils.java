package quickml.supervised.tree.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.scorers.Scorer;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.branchingConditions.BranchingConditions;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/23/15.
 */
public class SplittingUtils {

    public static <VC extends ValueCounter<VC>> Optional<SplitScore> splitSortedAttributeStats(AttributeStats<VC> attributeStats, Scorer<VC> scorer,
                                                                                               BranchingConditions<VC> branchingConditions,
                                                                                               AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy,
                                                                                               boolean doNotUseAttributeValuesWithInsuffientStatistics) {
        double bestScore = 0;
        int indexOfLastValueCounterInTrueSet = 0;
        double probabilityOfBeingInTrueSet = 0;
        boolean trueSetExists = false;

        List<VC> attributeValueStatsList = attributeStats.getStatsOnEachValue();
        VC falseSet = attributeStats.getAggregateStats();
        VC trueSet = falseSet.subtract(falseSet); //empty true Set

        scorer.setIntrinsicValue(attributeStats);
        scorer.setUnSplitScore(attributeStats.getAggregateStats());

        for (int i = 0; i < attributeValueStatsList.size() - 1; i++) {

            VC valueCounterForAttrVal = attributeValueStatsList.get(i);
            if (shouldWeIgnoreValueCounter(attributeValueIgnoringStrategy, doNotUseAttributeValuesWithInsuffientStatistics, valueCounterForAttrVal)) {
                continue;
            }

            trueSet = trueSet.add(valueCounterForAttrVal);
            falseSet = falseSet.subtract(valueCounterForAttrVal);

            //TODO Could optimize by knowing that all additional trial splits will fail once false set because small enough
            if (branchingConditions.isInvalidSplit(trueSet, falseSet, attributeStats.getAttribute())
                    || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(trueSet)
                    || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(falseSet)) {
                continue;
            }

            double thisScore = scorer.scoreSplit(trueSet, falseSet);
            if (branchingConditions.isInvalidSplit(thisScore)) {
                continue;
            }
            if (thisScore > bestScore) {
                bestScore = thisScore;
                indexOfLastValueCounterInTrueSet = i;
                probabilityOfBeingInTrueSet = trueSet.getTotal() / (trueSet.getTotal() + falseSet.getTotal());
                trueSetExists = true;
                branchingConditions.isInvalidSplit(trueSet, falseSet, attributeStats.getAttribute());
            }
        }
        if (!trueSetExists) {
            return Optional.absent();
        }
        Set<Object> trueSetVals = createTrueSetVals(indexOfLastValueCounterInTrueSet, attributeValueStatsList, attributeValueIgnoringStrategy, doNotUseAttributeValuesWithInsuffientStatistics);

        return Optional.of(new SplitScore(bestScore, indexOfLastValueCounterInTrueSet, probabilityOfBeingInTrueSet, trueSetVals));
    }

    private static <VC extends ValueCounter<VC>> Set<Object> createTrueSetVals(int indexOfLastTermStatsInTrueSet, List<VC> valueCounters, AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy, boolean doNotUseAttributeValuesWithInsuffientStatistics) {
        Set<Object> trueSetVals = Sets.newHashSet();
        for (int j = 0; j <= indexOfLastTermStatsInTrueSet; j++) {
            VC valueCounterForAttrVal = valueCounters.get(j);
            if (shouldWeIgnoreValueCounter(attributeValueIgnoringStrategy, doNotUseAttributeValuesWithInsuffientStatistics, valueCounterForAttrVal)) {
                continue;
            }
            trueSetVals.add(valueCounterForAttrVal.getAttrVal());
        }
        return trueSetVals;
    }

    private static <VC extends ValueCounter<VC>> boolean shouldWeIgnoreValueCounter(AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy, boolean doNotUseAttributeValuesWithInsuffientStatistics, VC valueCounterForAttrVal) {
        return attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(valueCounterForAttrVal)
                && doNotUseAttributeValuesWithInsuffientStatistics;
    }

    public static class SplitScore {
        public double score;
        public int indexOfLastTermStatsInTrueSet;
        public double probabilityOfBeingInTrueSet;
        public Set<Object> trueSet;

        public SplitScore(double score, int indexOfLastTermStatsInTrueSet, double probabilityOfBeingInTrueSet, Set<Object> trueSet) {
            this.score = score;
            this.indexOfLastTermStatsInTrueSet = indexOfLastTermStatsInTrueSet;
            this.probabilityOfBeingInTrueSet = probabilityOfBeingInTrueSet;
            this.trueSet = trueSet;
        }
    }
}
