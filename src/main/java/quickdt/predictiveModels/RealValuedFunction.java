package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface RealValuedFunction extends PredictiveModel<RealValuedPrediction> {
    public abstract RealValuedFunction predict(AbstractInstance instance);
}
