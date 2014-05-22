package quickdt.predictiveModels.featureEngineering;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;

import java.util.List;

/**
 * Created by chrisreeves on 5/22/14.
 */
public class UpdatableFeatureEngineeringPredictiveModelBuilder extends UpdatablePredictiveModelBuilder<FeatureEngineeredPredictiveModel> {
    private final FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder;

    public UpdatableFeatureEngineeringPredictiveModelBuilder(FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder) {
        this(featureEngineeringPredictiveModelBuilder, null);
    }

    public UpdatableFeatureEngineeringPredictiveModelBuilder(FeatureEngineeringPredictiveModelBuilder featureEngineeringPredictiveModelBuilder, FeatureEngineeredPredictiveModel model) {
        super(model);
        featureEngineeringPredictiveModelBuilder.updatable(true);
        this.featureEngineeringPredictiveModelBuilder = featureEngineeringPredictiveModelBuilder;
    }

    @Override
    public FeatureEngineeredPredictiveModel buildUpdatablePredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        return featureEngineeringPredictiveModelBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(FeatureEngineeredPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        featureEngineeringPredictiveModelBuilder.updatePredictiveModel(predictiveModel, newData, trainingData, splitNodes);
    }

    @Override
    public void stripData(FeatureEngineeredPredictiveModel predictiveModel) {
        featureEngineeringPredictiveModelBuilder.stripData(predictiveModel);
    }
}
