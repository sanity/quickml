package quickml.supervised;
import quickml.data.Instance;


/**
 * Created by chrisreeves on 5/22/14.
 */
public interface UpdatablePredictiveModelBuilder<R, PM extends PredictiveModel<R, ?>> extends PredictiveModelBuilder<R, PM>{

    public abstract void updatePredictiveModel(PM predictiveModel, Iterable<? extends Instance<R>> newData, boolean splitNodes);
    public abstract void stripData(PM predictiveModel);
}
