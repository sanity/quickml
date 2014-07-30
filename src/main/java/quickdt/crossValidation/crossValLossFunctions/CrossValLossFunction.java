package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

import java.util.List;

/**
 * Created by alexanderhawk on 4/24/14.
 */
public interface CrossValLossFunction<T extends PredictiveModel> {
    public abstract double getLoss(List<? extends AbstractInstance> crossValSet, T predictiveModel);
}
