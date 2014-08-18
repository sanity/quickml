package quickml.supervised.regressionModel;

import quickml.supervised.PredictiveModel;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface SingleVariableRealValuedFunction extends PredictiveModel<Double, Double> {
    public abstract Double predict(Double regressor);
}