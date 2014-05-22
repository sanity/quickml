package quickdt.predictiveModels.wrappedPredictiveModel;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.util.List;

/**
 * Created by chrisreeves on 5/22/14.
 */
public class WrappedPredictiveModelBuilder implements PredictiveModelBuilder<WrappedPredictiveModel> {
    protected PredictiveModelBuilder wrappedPredictiveModelBuilder;

    public WrappedPredictiveModelBuilder(PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder) {
        this.wrappedPredictiveModelBuilder = predictiveModelBuilder;
    }

    @Override
    public WrappedPredictiveModel buildPredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        return new WrappedPredictiveModel(wrappedPredictiveModelBuilder.buildPredictiveModel(trainingData));
    }

    @Override
    public WrappedPredictiveModelBuilder updatable(boolean updatable) {
        wrappedPredictiveModelBuilder.updatable(true);
        return this;
    }

    @Override
    public void updatePredictiveModel(WrappedPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        wrappedPredictiveModelBuilder.updatePredictiveModel(predictiveModel.predictiveModel, newData, trainingData, splitNodes);
    }

    @Override
    public void stripData(WrappedPredictiveModel predictiveModel) {
        wrappedPredictiveModelBuilder.stripData(predictiveModel.predictiveModel);
    }
}
