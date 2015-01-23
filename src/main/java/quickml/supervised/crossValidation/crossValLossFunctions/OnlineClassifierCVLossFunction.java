package quickml.supervised.crossValidation.crossValLossFunctions;

import quickml.data.PredictionMap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public abstract class OnlineClassifierCVLossFunction implements CrossValLossFunction<Serializable, PredictionMap> { // <CP extends ClassifierPrediction> implements CrossValLossFunction<CP> {
    protected double totalLoss = 0;
    double weightOfAllInstances = 0;

    protected abstract double getLossFromInstance(double probabilityOfCorrectInstance, double weight);

    public double getLoss(List<LabelPredictionWeight<Serializable, PredictionMap>> labelPredictionWeights) {
        totalLoss = 0;
        weightOfAllInstances = 0;
        for (LabelPredictionWeight<Serializable, PredictionMap> labelPredictionWeight : labelPredictionWeights) {
            PredictionMap classifierPrediction = labelPredictionWeight.getPrediction();
            double probabilityOfCorrectInstance = classifierPrediction.get(labelPredictionWeight.getLabel());
            totalLoss += getLossFromInstance(probabilityOfCorrectInstance, labelPredictionWeight.getWeight());
            weightOfAllInstances += labelPredictionWeight.getWeight();
        }
        return weightOfAllInstances > 0 ? totalLoss / weightOfAllInstances : 0;
    }
}
