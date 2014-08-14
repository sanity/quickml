package quickml.supervised.regressionModel;

import quickml.supervised.AbstractPredictiveModel;

import java.util.Map;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class MultiVariableRealValuedFunction extends AbstractPredictiveModel<Map<String, Double>, Double> {
    public abstract Double predict(Map<String, Double> regressor);
}
