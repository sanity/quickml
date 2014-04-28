package quickdt.crossValidation;

import com.google.common.base.Supplier;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public class RMSECrossValLoss extends OnlineCrossValLoss<RMSECrossValLoss> {

    private MSECrossValLoss mseCrossValLoss = new MSECrossValLoss();

    @Override
    public double getLossFromInstance(final double probabilityOfCorrectInstance, double weight) {
        return mseCrossValLoss.getLossFromInstance(probabilityOfCorrectInstance, weight);
    }

    @Override
    public int compareTo(final RMSECrossValLoss o) {
        return mseCrossValLoss.compareTo(o.mseCrossValLoss);
    }
    @Override
    public double getTotalLoss(List<AbstractInstance> crossValSet, PredictiveModel predictiveModel) {
        return Math.sqrt(super.getTotalLoss(crossValSet, predictiveModel));
    }

    @Override
    public String toString() {
        return "RMSE: "+ super.totalLoss;
    }
}
