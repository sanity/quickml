package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers;

import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldScorer;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldClassificationCounter;

import java.io.Serializable;

/**
 * A Scorer intended to estimate the impact on the Mean of the Squared Error (MSE)
 * of a branch existing versus not existing.  The value returned is the MSE
 * without the branch minus the MSE with the branch (so higher is better, as
 * is required by the scoreSplit() interface.
 */
public class MSEOldScorerWithCrossValidationCorrection implements OldScorer {

    private double normalizedParentMSE = 0;
    private OldClassificationCounter trainingInsetCC;
    private OldClassificationCounter trainingOutsetCC;
    private double totalTestWeight;

    public MSEOldScorerWithCrossValidationCorrection(OldClassificationCounter trainingSetParent, OldClassificationCounter testSetCCParent) {
        totalTestWeight = testSetCCParent.getTotal();
        normalizedParentMSE = getErrorOnTestSet(trainingSetParent, testSetCCParent) / totalTestWeight;
    }

    private double getErrorOnTestSet(OldClassificationCounter trainingSetCC, OldClassificationCounter testSetCC) {
        double totalTrainingWeight = trainingSetCC.getTotal();
        double mse = 0;
        for (Serializable label : testSetCC.getCounts().keySet()) {
            double labelProb = trainingSetCC.getCounts().get(label) / totalTrainingWeight;
            double labelError = 1.0 - labelProb;
            double labelOccurencesInTestSet = testSetCC.getCount(label);
            mse += labelError * labelError * labelOccurencesInTestSet;
        }
        return mse;
    }

    public void updateTrainingSetClassificationCounters(OldClassificationCounter trainingSetInset, OldClassificationCounter trainingSetOutset) {
        this.trainingInsetCC = trainingSetInset;
        this.trainingOutsetCC = trainingSetOutset;
    }

    @Override
    public double scoreSplit(final OldClassificationCounter testInsetCC, final OldClassificationCounter testOutSetCC) {
        double normalizedSplitMSE = (getErrorOnTestSet(trainingInsetCC, testInsetCC) + getErrorOnTestSet(trainingOutsetCC, testOutSetCC)) / (totalTestWeight);
        return normalizedParentMSE - normalizedSplitMSE;
    }


}
