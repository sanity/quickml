package quickml.supervised.tree.decisionTree.scorers;

import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.GRImbalancedScorer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by chrisreeves on 6/24/14.
 */
public class PenalizedGiniImpurityScorer extends GRImbalancedScorer<ClassificationCounter> {

    public PenalizedGiniImpurityScorer(double degreeOfGainRatioPenalty, double imbalancePenaltyPower, AttributeStats<ClassificationCounter> attributeStats) {
        super(degreeOfGainRatioPenalty, imbalancePenaltyPower, attributeStats);
    }

    @Override
    public double scoreSplit(ClassificationCounter a, ClassificationCounter b) {
        ClassificationCounter parent = ClassificationCounter.merge(a, b);
        double aGiniIndex = getGiniIndex(a) * a.getTotal() / parent.getTotal();
        double bGiniIndex = getGiniIndex(b) * b.getTotal() / parent.getTotal();
        double score = unSplitScore - aGiniIndex - bGiniIndex;
        return correctForGainRatio(score)*getPenaltyForImabalance(a, b);
    }

    @Override
    public double getUnSplitScore(ClassificationCounter a) {
        return getGiniIndex(a);

    }

    private double getGiniIndex(ClassificationCounter cc) {
        double sum = 0.0d;
        for (Map.Entry<Serializable, Double> e : cc.getCounts().entrySet()) {
            double error = (cc.getTotal() > 0) ? e.getValue() / cc.getTotal() : 0;
            sum += error * error;
        }
        return 1.0d - sum;
    }

    @Override
    public String toString() {
        return "GiniImpurity";
    }

}
