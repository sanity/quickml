package quickdt.crossValidation.crossValLossFunctions;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 2/28/14.
 */
public abstract class OnlineClassifierCVLossFunction implements CrossValLossFunction<Map<Serializable,Double>>{ // <CP extends ClassifierPrediction> implements CrossValLossFunction<CP> {
    protected double totalLoss = 0;
    double weightOfAllInstances = 0;
    protected abstract double getLossFromInstance(double probabilityOfCorrectInstance, double weight);
    public double getLoss(List<LabelPredictionWeight<Map<Serializable,Double>>> labelPredictionWeights) {
        totalLoss = 0;
        weightOfAllInstances = 0;
        for (LabelPredictionWeight<Map<Serializable, Double>> labelPredictionWeight : labelPredictionWeights) {
            Map<Serializable,Double> classifierPrediction = labelPredictionWeight.getPrediction();
            double probabilityOfCorrectInstance = classifierPrediction.get(labelPredictionWeight.getLabel());
            totalLoss += getLossFromInstance(probabilityOfCorrectInstance, labelPredictionWeight.getWeight());
            weightOfAllInstances += labelPredictionWeight.getWeight();
        }
        return weightOfAllInstances > 0 ? totalLoss / weightOfAllInstances : 0;
    }
}
