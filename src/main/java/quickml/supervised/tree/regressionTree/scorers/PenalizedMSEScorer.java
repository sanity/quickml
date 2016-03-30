package quickml.supervised.tree.regressionTree.scorers;


//TODO: fix oldScorers
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;
import quickml.supervised.tree.scorers.GRImbalancedScorer;

/**
 * A Scorer intended to estimate the impact on the Mean of the Squared Error (MSE)
 * of a branch existing versus not existing.  The value returned is the MSE
 * without the branch minus the MSE with the branch (so higher is better, as
 * is required by the scoreSplit() interface.
 */
public class PenalizedMSEScorer extends GRImbalancedScorer<MeanValueCounter> {

    @Override
    protected double getUnSplitScore(MeanValueCounter a) {
        return getTotalError(a)/a.getTotal();
    }

    public PenalizedMSEScorer(double degreeOfGainRatioPenalty, double imbalancePenaltyPower, AttributeStats<MeanValueCounter> attributeStats) {
        super(degreeOfGainRatioPenalty, imbalancePenaltyPower, attributeStats);
    }

    @Override
    public double scoreSplit(final MeanValueCounter a, final MeanValueCounter b) {
        double splitMSE = (getTotalError(a) + getTotalError(b)) / (a.getTotal() + b.getTotal());
        return correctForGainRatio(unSplitScore - splitMSE) * getPenaltyForImabalance(a, b);
    }

    private double getTotalError(MeanValueCounter mvc) {
        //below: total MSE for using the mvc as a leaf is Sum( (yi- mean)^2 ) = accumulatedSquares - mean^2 *numSamples
        double totalError = (mvc.getAccumulatedSquares() - mvc.getAccumulatedValue()*mvc.getAccumulatedValue()/mvc.getTotal());
        return totalError;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MSEScorer{");
        sb.append('}');
        return sb.toString();
    }
}
