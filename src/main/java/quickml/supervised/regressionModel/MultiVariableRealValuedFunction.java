package quickml.supervised.regressionModel;

import quickml.supervised.AbstractPredictiveModel;

import java.util.Map;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class MultiVariableRealValuedFunction extends AbstractPredictiveModel<double[], Double> {
    public abstract Double predict(double[] regressors);
}
