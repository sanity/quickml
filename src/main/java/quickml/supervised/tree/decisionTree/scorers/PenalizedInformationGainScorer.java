package quickml.supervised.tree.decisionTree.scorers;

import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.GRImbalancedScorer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by chrisreeves on 6/24/14.
 */
public class PenalizedInformationGainScorer extends GRImbalancedScorer<ClassificationCounter> {

    public PenalizedInformationGainScorer(double degreeOfGainRatioPenalty, double imbalancePenaltyPower, AttributeStats<ClassificationCounter> attributeStats) {
        super(degreeOfGainRatioPenalty, imbalancePenaltyPower, attributeStats);
    }

    @Override
    protected double getUnSplitScore(ClassificationCounter a) {
        return calculateEntropy(a);
    }

    @Override
    public double scoreSplit(ClassificationCounter a, ClassificationCounter b) {
        double aEntropy = calculateEntropy(a);
        double bEntropy = calculateEntropy(b);
        double ig = calculateGain(unSplitScore, aEntropy, bEntropy, a.getTotal(), b.getTotal());
        return correctForGainRatio(ig)*getPenaltyForImabalance(a, b);
    }

    private double calculateEntropy(ClassificationCounter cc) {
        double entropy = 0;

        for (Map.Entry<Serializable, Double> e : cc.getCounts().entrySet()) {
            if (e.getValue().equals(0.0)) {
                continue;
            }
            double error = (cc.getTotal() > 0) ? e.getValue() / cc.getTotal() : 0;
            entropy += -error * (Math.log(error) / Math.log(2));
        }

        return entropy;
    }

    private double calculateGain(double rootEntropy, double aEntropy, double bEntropy, double aSize, double bSize) {
        double aAdjustedEntropy = (aSize / (aSize+bSize)) * aEntropy;
        double bAdjustedEntropy = (bSize / (aSize+bSize)) * bEntropy;
        return rootEntropy - aAdjustedEntropy - bAdjustedEntropy;
    }

    @Override
    public String toString() {
        return "InformationGain";
    }
}