package quickml.supervised;

import quickml.data.Instance;

import java.io.Serializable;

/**
 * A supervised learning algorithm, which, given data, will generate a PredictiveModel.
 */
public interface PredictiveModelBuilder<R, PM extends PredictiveModel<R, ?>> {

    PM buildPredictiveModel(Iterable<Instance<R>> trainingData);
    PredictiveModelBuilder<R, PM> updatable(boolean updatable);
    void setID(Serializable id);
}
