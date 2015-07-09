package quickml.supervised.tree.scorers;


import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.io.Serializable;

/**
 * The scorerFactory is responsible for assessing the quality of a "split" of data.
 */
public abstract class GRImbalancedScorer<VC extends ValueCounter<VC>>  extends GRScorer<VC> implements Serializable{
    protected final double imbalancePenaltyPower;


    public GRImbalancedScorer(double degreeOfGainRatioPenalty, double imbalancePenaltyPower, AttributeStats<VC> attributeStats) {
        super(degreeOfGainRatioPenalty, attributeStats);
        this.imbalancePenaltyPower =imbalancePenaltyPower;
    }

    private double  getIntrinsicValue(AttributeStats<VC> attributeStats) {
        double intrinsicValue = 0;
        double attributeValProb = 0;

        for (VC valueCounter : attributeStats.getStatsOnEachValue()) {
            if (!valueCounter.isEmpty()) {  // if it is empty, it should not be considered.
                attributeValProb = valueCounter.getTotal() / attributeStats.getAggregateStats().getTotal();
                intrinsicValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
            }
        }

        return intrinsicValue;
    }


    protected double getPenaltyForImabalance(VC a, VC b) {
        return  1/Math.pow(Math.max(a.getTotal(), b.getTotal()), imbalancePenaltyPower);
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