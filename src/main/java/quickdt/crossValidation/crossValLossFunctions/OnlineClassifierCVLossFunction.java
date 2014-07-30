package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public abstract class OnlineClassifierCVLossFunction<T extends Classifier> implements ClassifierCVLossFunction<T> {
    double totalLoss = 0;
    double weightOfAllInstances = 0;
    protected abstract double getLossFromInstance(double probabilityOfCorrectInstance, double weight);
    public double getLoss(List<? extends AbstractInstance> crossValSet, T classifier) {
        totalLoss = 0;
        weightOfAllInstances = 0;
        for (AbstractInstance instance : crossValSet) {
            totalLoss += getLossFromInstance(classifier.getProbability(instance.getAttributes(), instance.getObserveredValue()), instance.getWeight());
            weightOfAllInstances += instance.getWeight();
        }
        return totalLoss / weightOfAllInstances;
    }

}
