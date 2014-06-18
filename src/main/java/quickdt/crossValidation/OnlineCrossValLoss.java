package quickdt.crossValidation;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public abstract class OnlineCrossValLoss<S extends CrossValLoss> implements Comparable<S>, CrossValLoss {
    double totalLoss = 0;
    double weightOfAllInstances = 0;
    protected abstract double getLossFromInstance(double probabilityOfCorrectInstance, double weight);
    public double getLoss(List<? extends AbstractInstance> crossValSet, PredictiveModel predictiveModel) {
        totalLoss = 0;
        weightOfAllInstances = 0;
        for (AbstractInstance instance : crossValSet) {
            totalLoss += getLossFromInstance(predictiveModel.getProbability(instance.getAttributes(), instance.getClassification()), instance.getWeight());
            weightOfAllInstances += instance.getWeight();
        }
        return totalLoss / weightOfAllInstances;
    }

}
