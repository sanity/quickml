package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;
import quickdt.predictiveModels.ClassifierPrediction;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public class ClassifierRMSECrossValLossFunction extends ClassifierMSECrossValLossFunction {

    private ClassifierMSECrossValLossFunction mseCrossValLoss = new ClassifierMSECrossValLossFunction();

    @Override
    public double getLossFromInstance(final double probabilityOfCorrectInstance, double weight) {
        return mseCrossValLoss.getLossFromInstance(probabilityOfCorrectInstance, weight);
    }

    @Override
    public double getLoss(List<LabelPredictionWeight<ClassifierPrediction>> labelPredictionWeights) {
        return Math.sqrt(super.getLoss(labelPredictionWeights));
    }

    @Override
    public String toString() {
        return "RMSE: "+ super.totalLoss;
    }
}
