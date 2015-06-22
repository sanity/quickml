package quickml.supervised.tree.decisionTree.scorers;


//TODO: fix scorers
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.scorers.Scorer;

import java.util.Map;

/**
 * A Scorer intended to estimate the impact on the Mean of the Squared Error (MSE)
 * of a branch existing versus not existing.  The value returned is the MSE
 * without the branch minus the MSE with the branch (so higher is better, as
 * is required by the scoreSplit() interface.
 */
public class MSEScorer extends Scorer<ClassificationCounter> {
    private final double crossValidationInstanceCorrection;

    @Override
    public void setUnSplitScore(ClassificationCounter a) {
        unSplitScore = getTotalError(a);   
    }

    @Override
    public Scorer<ClassificationCounter> createScorer() {
        return null;
    }

    public MSEScorer(CrossValidationCorrection crossValidationCorrection) {
        if (crossValidationCorrection.equals(CrossValidationCorrection.TRUE)) {
            crossValidationInstanceCorrection = 1.0;
        } else {
            crossValidationInstanceCorrection = 0.0;
        }
    }

    @Override
    public double scoreSplit(final ClassificationCounter a, final ClassificationCounter b) {
        double splitMSE = (getTotalError(a) + getTotalError(b)) / (a.getTotal() + b.getTotal());
        return correctScoreForGainRatioPenalty(unSplitScore - splitMSE);
    }

    private double getTotalError(ClassificationCounter cc) {
        double totalError = 0;
        for (Map.Entry<Object, Double> e : cc.getCounts().entrySet()) {
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
        sb.append("cvic=").append(crossValidationInstanceCorrection);
        sb.append('}');
        return sb.toString();
    }
}
