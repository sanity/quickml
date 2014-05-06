package quickdt.crossValidation;

import org.apache.commons.lang.NotImplementedException;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

/**
 * Created by ian on 2/28/14.
 */
public abstract class OnlineCrossValLoss<S extends OnlineCrossValLoss> implements CrossValLoss<S>, Comparable<S> {
    public void addLoss(AbstractInstance instance, PredictiveModel predictiveModel) {
        addLossFromInstance(predictiveModel.getProbability(instance.getAttributes(), instance.getClassification()), instance.getWeight());
    }

    public double getAverageLoss() {
        throw new NotImplementedException("Average loss not implemented for OnlineCrossValLoss");
    }

    protected abstract void addLossFromInstance(double probabilityOfCorrectInstance, double weight);
}
