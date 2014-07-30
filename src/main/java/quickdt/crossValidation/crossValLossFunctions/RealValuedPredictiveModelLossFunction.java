package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.RealValuedFunction;

import java.util.List;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface RealValuedPredictiveModelLossFunction<T extends RealValuedFunction> extends CrossValLossFunction<T>{
    @Override
    double getLoss(List<? extends AbstractInstance> crossValSet, T predictiveModel);
}
