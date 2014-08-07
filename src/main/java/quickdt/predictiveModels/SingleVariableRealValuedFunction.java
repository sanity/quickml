package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class SingleVariableRealValuedFunction extends AbstractPredictiveModel<Double, Double> implements PredictiveModel<Double, Double> {
    public abstract Double predict(Double regressor);
}