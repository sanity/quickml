package quickdt.predictiveModels.featureEngineering;

import com.google.common.collect.Iterables;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

/**
 * Created by ian on 5/20/14.
 */
public abstract class FeatureEngineeringPredictiveModelBuilder implements PredictiveModelBuilder<FeatureEngineeredPredictiveModel> {

    private final PredictiveModelBuilder<?> wrappedBuilder;

    public FeatureEngineeringPredictiveModelBuilder(PredictiveModelBuilder<?> wrappedBuilder) {
        this.wrappedBuilder = wrappedBuilder;
    }

    @Override
    public FeatureEngineeredPredictiveModel buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        AttributesEnricher attributesEnricher = createAttributesEnricher(trainingData);

        final Iterable<Instance> enrichedTrainingData = Iterables.transform(trainingData, new InstanceEnricher(attributesEnricher));

        PredictiveModel predictiveModel = wrappedBuilder.buildPredictiveModel(enrichedTrainingData);

        return new FeatureEngineeredPredictiveModel(predictiveModel, attributesEnricher);
    }

    public abstract AttributesEnricher createAttributesEnricher(final Iterable<? extends AbstractInstance> trainingData);
}
