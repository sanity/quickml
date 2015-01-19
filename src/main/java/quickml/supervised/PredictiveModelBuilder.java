package quickml.supervised;

import quickml.data.Instance;

import java.io.Serializable;

/**
 * A supervised learning algorithm, which, given data, will generate a PredictiveModel.
 */
public interface PredictiveModelBuilder<INPUT, OUTPUT, PREDICTION, PM extends PredictiveModel<INPUT, OUTPUT>> {

    PM buildPredictiveModel(Iterable<? extends Instance<INPUT, OUTPUT>> trainingData);
    PredictiveModelBuilder<INPUT, OUTPUT,PREDICTION,  PM> updatable(boolean updatable);
    void setID(Serializable id);
}
