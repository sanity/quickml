package quickdt.predictiveModels.featureEngineering;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.util.List;

/**
 * Created by ian on 5/20/14.
 */
public class FeatureEngineeringPredictiveModelBuilder implements PredictiveModelBuilder<FeatureEngineeredPredictiveModel> {

    private final PredictiveModelBuilder<?> wrappedBuilder;
    private final List<? extends AttributesEnricherBuildStrategy> enrichStrategies;

    public FeatureEngineeringPredictiveModelBuilder(PredictiveModelBuilder<?> wrappedBuilder, List<? extends AttributesEnricherBuildStrategy> enrichStrategies) {
        this.wrappedBuilder = wrappedBuilder;
        this.enrichStrategies = enrichStrategies;
    }

    @Override
    public FeatureEngineeredPredictiveModel buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        List<AttributesEnricher> enrichers = Lists.newArrayListWithExpectedSize(enrichStrategies.size());

        for (AttributesEnricherBuildStrategy enrichStrategy : enrichStrategies) {
            enrichers.add(enrichStrategy.build(trainingData));
        }

        final Iterable<Instance> enrichedTrainingData = Iterables.transform(trainingData, new InstanceEnricher(enrichers));

        PredictiveModel predictiveModel = wrappedBuilder.buildPredictiveModel(enrichedTrainingData);

        return new FeatureEngineeredPredictiveModel(predictiveModel, enrichers);
    }
}
