package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;

import java.util.List;

/**
 * Created by chrisreeves on 5/22/14.
 */
public interface UpdatablePredictiveModelBuilder<R extends, PM extends PredictiveModel<R, ?>> extends PredictiveModelBuilder<R, PM>{
    public void updatePredictiveModel(PM predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes);
    public void stripData(PM predictiveModel);
}
