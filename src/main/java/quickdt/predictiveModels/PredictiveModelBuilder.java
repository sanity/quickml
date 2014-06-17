package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PredictiveModelBuilder<PM extends PredictiveModel> {
    PM buildPredictiveModel(Iterable<? extends AbstractInstance> trainingData);
    PredictiveModelBuilder<PM> updatable(boolean updatable);
    void setID(Serializable id);
}
