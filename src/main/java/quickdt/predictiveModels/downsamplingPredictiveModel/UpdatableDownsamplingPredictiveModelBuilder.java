package quickdt.predictiveModels.downsamplingPredictiveModel;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;

import java.util.List;

/**
 * Created by chrisreeves on 5/21/14.
 */
public class UpdatableDownsamplingPredictiveModelBuilder extends UpdatablePredictiveModelBuilder<DownsamplingPredictiveModel> {
    private final DownsamplingPredictiveModelBuilder downsamplingPredictiveModelBuilder;

    public UpdatableDownsamplingPredictiveModelBuilder(DownsamplingPredictiveModelBuilder DownsamplingPredictiveModelBuilder) {
        this(DownsamplingPredictiveModelBuilder, null);
    }

    public UpdatableDownsamplingPredictiveModelBuilder(DownsamplingPredictiveModelBuilder downsamplingPredictiveModelBuilder1, DownsamplingPredictiveModel DownsamplingPredictiveModel) {
        super(DownsamplingPredictiveModel);
        this.downsamplingPredictiveModelBuilder = downsamplingPredictiveModelBuilder1.updatable(true);
    }

    @Override
    public DownsamplingPredictiveModel buildUpdatablePredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return downsamplingPredictiveModelBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(DownsamplingPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        downsamplingPredictiveModelBuilder.updatePredictiveModel(predictiveModel, newData, trainingData, splitNodes);
    }

    @Override
    public void stripData(DownsamplingPredictiveModel predictiveModel) {
        downsamplingPredictiveModelBuilder.stripData(predictiveModel);
    }

}