package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;
import quickdt.predictiveModels.Prediction;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public abstract class OnlineClassifierCVLossFunction<P extends Prediction, I extends AbstractInstance> implements CrossValLossFunction<P,I> {
    double totalLoss = 0;
    double weightOfAllInstances = 0;
    protected abstract double getLossFromInstance(InstancePredictionPair<P, I> instancePredictionPair);
    public double getLoss(List<InstancePredictionPair<P, I>> instancePredictionPairs) {
        totalLoss = 0;
        weightOfAllInstances = 0;
        for (InstancePredictionPair instancePredictionPair : instancePredictionPairs) {
            totalLoss += getLossFromInstance(instancePredictionPair);
            weightOfAllInstances += instancePredictionPair.instance.getWeight();
        }
        return totalLoss / weightOfAllInstances;
    }

}
