package quickml.supervised.tree.scorers;


import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;

/**
 * The scorerFactory is responsible for assessing the quality of a "split" of data.
 */
public abstract class GRScorer<VC extends ValueCounter<VC>> implements Scorer<VC>, Serializable {
    protected final double degreeOfGainRatioPenalty;
    protected final double intrinsicValue;
    protected final double unSplitScore;

    public GRScorer(double degreeOfGainRatioPenalty, AttributeStats<VC> attributeStats) {
        this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        this.intrinsicValue = getIntrinsicValue(attributeStats);
        this.unSplitScore = getUnSplitScore(attributeStats.getAggregateStats());
    }

    private double getIntrinsicValue(AttributeStats<VC> attributeStats) {
        double intrinsicValue = 0;
        double attributeValProb = 0;
        if (attributeStats.getStatsOnEachValue() != null && !attributeStats.getStatsOnEachValue().isEmpty()) {
            for (VC valueCounter : attributeStats.getStatsOnEachValue()) {
                if (!valueCounter.isEmpty()) {  // if it is empty, it should not be considered.
                    attributeValProb = valueCounter.getTotal() / attributeStats.getAggregateStats().getTotal();
                    intrinsicValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
                }
            }
        } else {
            intrinsicValue = 1.0;
        }


        return intrinsicValue==0.0 ? 1.0 : intrinsicValue;
    }

    /**
     * @return A score, where a higher value indicates a better split. A value
     * of 0 being the lowest, and indicating no value.
     */

    protected abstract double getUnSplitScore(VC a);

    protected double correctForGainRatio(double uncorrectedScore) {
        /** call this method from score split only degreeOfGainRatioPenalty is non zero*/
        return uncorrectedScore * (1 - degreeOfGainRatioPenalty) + degreeOfGainRatioPenalty * (uncorrectedScore / intrinsicValue);
    }

}