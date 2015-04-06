package quickml.supervised.classifier.tree.decisionTree.scorers;

import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;

import java.io.Serializable;

/**
 * A Scorer intended to estimate the impact on the Mean of the Squared Error (MSE)
 * of a branch existing versus not existing.  The value returned is the MSE
 * without the branch minus the MSE with the branch (so higher is better, as
 * is required by the scoreSplit() interface.
 */
public class MSEScorerWithCrossValidationCorrection implements Scorer {

    private double normalizedParentMSE = 0;
    private ClassificationCounter trainingInsetCC;
    private ClassificationCounter trainingOutsetCC;
    private double totalTestWeight;

    public MSEScorerWithCrossValidationCorrection(ClassificationCounter trainingSetParent, ClassificationCounter testSetCCParent) {
        totalTestWeight = testSetCCParent.getTotal();
        normalizedParentMSE = getErrorOnTestSet(trainingSetParent, testSetCCParent) / totalTestWeight;
    }

    private double getErrorOnTestSet(ClassificationCounter trainingSetCC, ClassificationCounter testSetCC) {
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

    public void updateTrainingSetClassificationCounters(ClassificationCounter trainingSetInset, ClassificationCounter trainingSetOutset) {
        this.trainingInsetCC = trainingSetInset;
        this.trainingOutsetCC = trainingSetOutset;
    }

    @Override
    public double scoreSplit(final ClassificationCounter testInsetCC, final ClassificationCounter testOutSetCC) {
        double normalizedSplitMSE = (getErrorOnTestSet(trainingInsetCC, testInsetCC) + getErrorOnTestSet(trainingOutsetCC, testOutSetCC)) / (totalTestWeight);
        return normalizedParentMSE - normalizedSplitMSE;
    }


}
