package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PredictiveModelBuilder<R extends Serializable, PM extends PredictiveModel<R, ?>> {
    PM buildPredictiveModel(Iterable<? extends AbstractInstance<R>> trainingData);
    PredictiveModelBuilder<R, PM> updatable(boolean updatable);
    void setID(Serializable id);
}
