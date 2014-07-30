package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface RealValuedFunction extends PredictiveModel<Double> {
    public abstract Double predict(AbstractInstance instance);
}
