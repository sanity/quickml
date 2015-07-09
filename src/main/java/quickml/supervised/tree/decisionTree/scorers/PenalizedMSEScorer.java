package quickml.supervised.tree.decisionTree.scorers;


//TODO: fix oldScorers
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.GRImbalancedScorer;

import java.io.Serializable;
import java.util.Map;

/**
 * A Scorer intended to estimate the impact on the Mean of the Squared Error (MSE)
 * of a branch existing versus not existing.  The value returned is the MSE
 * without the branch minus the MSE with the branch (so higher is better, as
 * is required by the scoreSplit() interface.
 */
public class PenalizedMSEScorer extends GRImbalancedScorer<ClassificationCounter> {

    @Override
    protected double getUnSplitScore(ClassificationCounter a) {
        return getTotalError(a);
    }

    public PenalizedMSEScorer(double degreeOfGainRatioPenalty, double imbalancePenaltyPower, AttributeStats<ClassificationCounter> attributeStats) {
        super(degreeOfGainRatioPenalty, imbalancePenaltyPower, attributeStats);
    }

    @Override
    public double scoreSplit(final ClassificationCounter a, final ClassificationCounter b) {
        double splitMSE = (getTotalError(a) + getTotalError(b)) / (a.getTotal() + b.getTotal());
        return correctForGainRatio(unSplitScore - splitMSE) * getPenaltyForImabalance(a, b);
    }

    private double getTotalError(ClassificationCounter cc) {
        double totalError = 0;
        for (Map.Entry<Serializable, Double> e : cc.getCounts().entrySet()) {
            double error = (cc.getTotal()>0) ? 1.0 - e.getValue()/cc.getTotal() : 0;
            double errorSquared = error*error;
            totalError += errorSquared * e.getValue();
        }
        return totalError;
    }

    public enum CrossValidationCorrection {
        TRUE, FALSE
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MSEScorer{");
        sb.append('}');
        return sb.toString();
    }
}
