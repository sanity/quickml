package quickdt.predictiveModels.attributeCombiner;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;

import java.util.List;

/**
 * Created by chrisreeves on 5/22/14.
 */
public class UpdatableAttributeCombinerPredictiveModelBuilder extends UpdatablePredictiveModelBuilder<AttributeCombinerPredictiveModel> {

    private final AttributeCombinerModelBuilder attributeCombinerModelBuilder;

    public UpdatableAttributeCombinerPredictiveModelBuilder(AttributeCombinerModelBuilder attributeCombinerModelBuilder) {
        this(attributeCombinerModelBuilder, null);
    }

    public UpdatableAttributeCombinerPredictiveModelBuilder(AttributeCombinerModelBuilder attributeCombinerModelBuilder, AttributeCombinerPredictiveModel predictiveModel) {
        super(predictiveModel);
        attributeCombinerModelBuilder.updatable(true);
        this.attributeCombinerModelBuilder = attributeCombinerModelBuilder;
    }

    @Override
    public AttributeCombinerPredictiveModel buildUpdatablePredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        return attributeCombinerModelBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(AttributeCombinerPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        attributeCombinerModelBuilder.updatePredictiveModel(predictiveModel, newData, trainingData, splitNodes);
    }

    @Override
    public void stripData(AttributeCombinerPredictiveModel predictiveModel) {
        attributeCombinerModelBuilder.stripData(predictiveModel);
    }
}
