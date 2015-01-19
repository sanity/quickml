package quickml.supervised;
import quickml.data.Instance;


/**
 * Created by chrisreeves on 5/22/14.
 */
public interface UpdatablePredictiveModelBuilder<INPUT, OUTPUT, PREDICTION, PM extends PredictiveModel<INPUT, OUTPUT>> extends PredictiveModelBuilder<INPUT, OUTPUT, PREDICTION, PM>{

    public abstract void updatePredictiveModel(PM predictiveModel, Iterable<? extends Instance<INPUT, OUTPUT>> newData, boolean splitNodes);
    public abstract void stripData(PM predictiveModel);
}
