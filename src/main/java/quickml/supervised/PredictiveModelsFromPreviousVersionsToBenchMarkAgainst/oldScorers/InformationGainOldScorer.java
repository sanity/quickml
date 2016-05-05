package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers;

import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldScorer;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldClassificationCounter;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by chrisreeves on 6/24/14.
 */
public class InformationGainOldScorer implements OldScorer {

    @Override
    public double scoreSplit(OldClassificationCounter a, OldClassificationCounter b) {
        double parentEntropy = calculateEntropy(OldClassificationCounter.merge(a, b));
        double aEntropy = calculateEntropy(a);
        double bEntropy = calculateEntropy(b);
        return calculateGain(parentEntropy, aEntropy, bEntropy, a.getTotal(), b.getTotal());
    }

    private double calculateEntropy(OldClassificationCounter cc) {
        double entropy = 0;

        for (Map.Entry<Serializable, Double> e : cc.getCounts().entrySet()) {
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