package quickml.supervised.tree.branchFinders;

import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.nodes.Node;
import quickml.scorers.Scorer;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.branchingConditions.BranchingConditions;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/23/15.
 */
public class SplittingUtils {

public static <VC extends ValueCounter<VC>> SplitScore splitSortedAttributeStats(AttributeStats<VC> attributeStats, Scorer<VC> scorer,
                                                                               BranchingConditions<VC> branchingConditions,
                                                                               AttributeValueIgnoringStrategy<VC> attributeValueIgnoringStrategy) {
    double bestScore = 0;
    int indexOfLastTermStatsInTrueSet = 0;
    double probabilityOfBeingInTrueSet = 0;

    List<VC> attributeValueStatsList = attributeStats.getStatsOnEachValue();
    VC falseSet = attributeStats.getAggregateStats();
    VC trueSet = falseSet.subtract(falseSet); //empty true Set

    scorer.setIntrinsicValue(attributeStats);
    scorer.setUnSplitScore(attributeStats.getAggregateStats());

    for (int i = 0; i < attributeValueStatsList.size()-1; i++) {

        VC valueCounterForAttrVal = attributeValueStatsList.get(i);
        if( attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(valueCounterForAttrVal)) {
            continue;
        }

        trueSet = trueSet.add(valueCounterForAttrVal);
        falseSet = falseSet.subtract(valueCounterForAttrVal);
        if (branchingConditions.isInvalidSplit(trueSet, falseSet, attributeStats.getAttribute()) || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(trueSet) || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(falseSet)) {
            continue;
        }

        double thisScore = scorer.scoreSplit(trueSet, falseSet);
        if (thisScore > bestScore && !branchingConditions.isInvalidSplit(thisScore)) {
            bestScore = thisScore;
            indexOfLastTermStatsInTrueSet = i;
            probabilityOfBeingInTrueSet = trueSet.getTotal() / (trueSet.getTotal() + falseSet.getTotal());
        }
    }
    Set<Object> trueSetVals = createTrueSetVals(indexOfLastTermStatsInTrueSet, attributeValueStatsList);

    return new SplitScore(bestScore, indexOfLastTermStatsInTrueSet, probabilityOfBeingInTrueSet, trueSetVals);
}

    private static <TS extends ValueCounter<TS>> Set<Object> createTrueSetVals(int indexOfLastTermStatsInTrueSet, List<TS> termStats) {
        Set<Object> trueSetVals = Sets.newHashSet();
        for(int j =0; j<=indexOfLastTermStatsInTrueSet; j++) {
            trueSetVals.add(termStats.get(j).getAttrVal());
        }
        return trueSetVals;
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
