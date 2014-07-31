package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;
import quickdt.predictiveModels.Prediction;
import quickdt.predictiveModels.PredictiveModel;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public abstract class OnlineClassifierCVLossFunction<C extends Classifier> implements CrossValLossFunction<C> {
    protected double totalLoss = 0;
    double weightOfAllInstances = 0;
    protected abstract double getLossFromInstance(double probabilityOfCorrectInstance, double weight);
    public double getLoss(List<? extends AbstractInstance> instances, C classifier) {
        totalLoss = 0;
        weightOfAllInstances = 0;
        for (AbstractInstance instance : instances) {
            double probabilityOfCorrectInstance = classifier.getProbability(instance.getAttributes(), instance.getLabel());
            totalLoss += getLossFromInstance(probabilityOfCorrectInstance, instance.getWeight());
            weightOfAllInstances += instance.getWeight();
        }
        return totalLoss / weightOfAllInstances;
    }

}
