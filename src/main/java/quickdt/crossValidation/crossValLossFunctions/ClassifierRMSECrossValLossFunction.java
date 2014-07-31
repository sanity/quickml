package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public class ClassifierRMSECrossValLossFunction<C extends Classifier> extends ClassifierMSECrossValLossFunction<C> {

    private ClassifierMSECrossValLossFunction mseCrossValLoss = new ClassifierMSECrossValLossFunction();

    @Override
    public double getLossFromInstance(final double probabilityOfCorrectInstance, double weight) {
        return mseCrossValLoss.getLossFromInstance(probabilityOfCorrectInstance, weight);
    }

    @Override
    public double getLoss(List<? extends AbstractInstance> crossValSet, C classifier) {
        return Math.sqrt(super.getLoss(crossValSet, classifier));
    }

    @Override
    public String toString() {
        return "RMSE: "+ super.totalLoss;
    }
}
