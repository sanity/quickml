package quickdt.predictiveModels;

import quickdt.data.Instance;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PredictiveModelBuilder<R, PM extends PredictiveModel<R, ?>> {
//<<<<<<< HEAD
//    PM buildPredictiveModel(Iterable<? extends AbstractInstance<? extends R>> trainingData);
//    PredictiveModelBuilder<? extends R, ? extends PM> updatable(boolean updatable);
//=======

    PM buildPredictiveModel(Iterable<? extends Instance<R>> trainingData);
    PredictiveModelBuilder<R, PM> updatable(boolean updatable);
//>>>>>>> origin/regRefactorTake2-ian
    void setID(Serializable id);
}
