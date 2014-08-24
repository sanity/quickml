package quickml.supervised.regressionModel;


import quickml.supervised.PredictiveModel;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface MultiVariableRealValuedFunction extends PredictiveModel<double[], Double> {
    public abstract Double predict(double[] regressors);
}
