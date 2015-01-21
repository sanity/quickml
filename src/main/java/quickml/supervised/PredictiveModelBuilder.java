package quickml.supervised;

import quickml.data.Instance;

import java.io.Serializable;

/**
 * A supervised learning algorithm, which, given data, will generate a PredictiveModel.
 */
public interface PredictiveModelBuilder<R, PM extends PredictiveModel<R, ?>> {

    PM buildPredictiveModel(Iterable<? extends Instance<R>> trainingData);
    void setID(Serializable id);
}
