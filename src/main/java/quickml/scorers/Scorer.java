package quickml.scorers;


import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.io.Serializable;
import java.util.Map;

import static quickml.supervised.tree.constants.ForestOptions.*;

/**
 * The scorer is responsible for assessing the quality of a "split" of data.
 */
public abstract class Scorer<VC extends ValueCounter<VC>>  implements Serializable{
    public static final double NO_SCORE = Double.MIN_VALUE;
    protected double degreeOfGainRatioPenalty;
    protected double imbalancePenaltyPower = 0;
    protected double intrinsicValue;
    protected double unSplitScore;


    public Scorer() {
    }

    public Scorer(double degreeOfGainRatioPenalty) {
        this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
    }

    public Scorer(double degreeOfGainRatioPenalty, double imbalancePenaltyPower) {
        this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        this.imbalancePenaltyPower = imbalancePenaltyPower;
    }

    public void setIntrinsicValue(AttributeStats<VC> attributeStats) {
        double intrinsicValue = 0;
        double attributeValProb = 0;

        for (VC termStatistics : attributeStats.getStatsOnEachValue()) {
            attributeValProb = termStatistics.getTotal() / attributeStats.getAggregateStats().getTotal();
            intrinsicValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
        }

        this.intrinsicValue = intrinsicValue;
    }

    public double getPenaltyFactorForImabalance(VC a, VC b) {
        return 1/Math.pow(Math.max(a.getTotal(), b.getTotal()), imbalancePenaltyPower);
    }
    /**
     * @return A score, where a higher value indicates a better split. A value
     * of 0 being the lowest, and indicating no value.
     */
    public abstract double scoreSplit(VC a, VC b);

    public abstract void setUnSplitScore(VC a);

    protected double correctScoreForGainRatioPenalty(double uncorrectedScore) {
        /** call this method from score split only degreeOfGainRatioPenalty is non zero*/
        return uncorrectedScore * (1 - degreeOfGainRatioPenalty) + degreeOfGainRatioPenalty * (uncorrectedScore / intrinsicValue);
    }

    public void update(Map<String, Serializable> cfg) {
        if (cfg.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY.name()))
            degreeOfGainRatioPenalty = (Double) cfg.get(DEGREE_OF_GAIN_RATIO_PENALTY.name());
        if (cfg.containsKey(IMBALANCE_PENALTY_POWER.name()))
            imbalancePenaltyPower = (Double) cfg.get(IMBALANCE_PENALTY_POWER.name());
    }

    public synchronized Scorer<VC> copy() {
        Scorer<VC> copy = createScorer();
        copy.imbalancePenaltyPower = this.imbalancePenaltyPower;
        copy.degreeOfGainRatioPenalty = this.degreeOfGainRatioPenalty;
        return copy;
    }

    public abstract Scorer<VC> createScorer();


}