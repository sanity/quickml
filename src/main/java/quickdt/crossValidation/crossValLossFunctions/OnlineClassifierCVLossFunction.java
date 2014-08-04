package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;
import quickdt.predictiveModels.ClassifierPrediction;
import quickdt.predictiveModels.Prediction;
import quickdt.predictiveModels.PredictiveModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public abstract class OnlineClassifierCVLossFunction implements CrossValLossFunction<ClassifierPrediction>{ // <CP extends ClassifierPrediction> implements CrossValLossFunction<CP> {
    protected double totalLoss = 0;
    double weightOfAllInstances = 0;
    protected abstract double getLossFromInstance(double probabilityOfCorrectInstance, double weight);
    public double getLoss(List<LabelPredictionWeight<ClassifierPrediction>> labelPredictionWeights) {
        totalLoss = 0;
        weightOfAllInstances = 0;
        for (LabelPredictionWeight<ClassifierPrediction> labelPredictionWeight : labelPredictionWeights) {
            ClassifierPrediction classifierPrediction = labelPredictionWeight.getPrediction();
            double probabilityOfCorrectInstance = classifierPrediction.getPredictionForLabel(labelPredictionWeight.getLabel());
            totalLoss += getLossFromInstance(probabilityOfCorrectInstance, labelPredictionWeight.getWeight());
            weightOfAllInstances += labelPredictionWeight.getWeight();
        }
        return totalLoss / weightOfAllInstances;
    }
}
