package quickml.supervised;

import quickml.data.Instance;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PredictiveModelBuilder<R, PM extends PredictiveModel<R, ?>> {

    PM buildPredictiveModel(Iterable<Instance<R>> trainingData);
    PredictiveModelBuilder<R, PM> updatable(boolean updatable);
    void setID(Serializable id);
}
