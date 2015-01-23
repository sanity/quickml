package quickml.supervised.crossValidation.crossValLossFunctions;

import quickml.data.PredictionMap;

import java.io.Serializable;
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
    public double getLoss(List<LabelPredictionWeight<Serializable, PredictionMap>> labelPredictionWeights) {
        return Math.sqrt(super.getLoss(labelPredictionWeights));
    }

    @Override
    public String toString() {
        return "RMSE: "+ super.totalLoss;
    }
}
