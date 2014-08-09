package quickdt.predictiveModels;

import java.util.Map;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class MultiVariableRealValuedFunction extends AbstractPredictiveModel<Double, Double> {
    public abstract Double predict(Map<String, Double> regressor);
}
