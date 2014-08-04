package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class RealValuedFunction extends AbstractPredictiveModel<RealValuedFunctionPrediction> implements PredictiveModel<RealValuedFunctionPrediction> {
    public abstract RealValuedFunction predict(AbstractInstance instance);
}
