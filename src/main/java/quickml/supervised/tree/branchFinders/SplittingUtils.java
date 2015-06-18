package quickml.supervised.tree.branchFinders;

import com.google.common.collect.Sets;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.nodes.AttributeStats;
import quickml.supervised.tree.terminationConditions.BranchingConditions;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/23/15.
 */
public class SplittingUtils {

public static <TS extends ValueCounter<TS>> SplitScore splitSortedAttributeStats(AttributeStats<TS> attributeStats, Scorer<TS> scorer,
                                                                               BranchingConditions<TS> branchingConditions,
                                                                               AttributeValueIgnoringStrategy<TS> attributeValueIgnoringStrategy) {
    double bestScore = 0;
    int indexOfLastTermStatsInTrueSet = 0;
    double probabilityOfBeingInTrueSet = 0;

    List<TS> termStats = attributeStats.getTermStats();
    TS falseSet = attributeStats.getAggregateStats();
    TS trueSet = falseSet.subtract(falseSet); //empty true Set

    scorer.setIntrinsicValue(attributeStats);
    scorer.setUnSplitScore(attributeStats.getAggregateStats());

    for (int i = 0; i < termStats.size()-1; i++) {

        TS termStatsForTrialAttrVal = termStats.get(i);
        //for numeric branch this may not be wanted, but don't see how it can hurt.
        if( attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(termStatsForTrialAttrVal)) {
            continue;
        }

        trueSet = trueSet.add(termStatsForTrialAttrVal);
        falseSet = falseSet.subtract(termStatsForTrialAttrVal);
        if (branchingConditions.isInvalidSplit(trueSet, falseSet) || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(trueSet) || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(falseSet)) {
            continue;
        }

        double thisScore = scorer.scoreSplit(trueSet, falseSet);
        if (thisScore > bestScore && !branchingConditions.isInvalidSplit(thisScore)) {
            bestScore = thisScore;
            indexOfLastTermStatsInTrueSet = i;
            probabilityOfBeingInTrueSet = trueSet.getTotal() / (trueSet.getTotal() + falseSet.getTotal());
        }
    }
    Set<Object> trueSetVals = createTrueSetVals(indexOfLastTermStatsInTrueSet, termStats);

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
        double score;
        int indexOfLastTermStatsInTrueSet;
        double probabilityOfBeingInTrueSet;
        Set<Object> trueSet;

        public SplitScore(double score, int indexOfLastTermStatsInTrueSet, double probabilityOfBeingInTrueSet, Set<Object> trueSet) {
            this.score = score;
            this.indexOfLastTermStatsInTrueSet = indexOfLastTermStatsInTrueSet;
            this.probabilityOfBeingInTrueSet = probabilityOfBeingInTrueSet;
            this.trueSet = trueSet;
        }
    }
}
