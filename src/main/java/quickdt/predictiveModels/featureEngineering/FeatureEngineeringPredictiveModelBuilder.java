package quickdt.predictiveModels.featureEngineering;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.wrappedPredictiveModel.WrappedPredictiveModelBuilder;

import java.util.List;

/**
 * A PredictiveModelBuilder that attempts to
 */
public class FeatureEngineeringPredictiveModelBuilder extends WrappedPredictiveModelBuilder {
    private static final  Logger logger =  LoggerFactory.getLogger(FeatureEngineeringPredictiveModelBuilder.class);

    private final List<? extends AttributesEnrichStrategy> enrichStrategies;

    public FeatureEngineeringPredictiveModelBuilder(PredictiveModelBuilder<?> wrappedBuilder, List<? extends AttributesEnrichStrategy> enrichStrategies) {
        super(wrappedBuilder);
        if (enrichStrategies.isEmpty()) {
            logger.warn("Won't do anything if no AttributesEnrichStrategies are provided");
        }
        this.enrichStrategies = enrichStrategies;
    }

    @Override
    public FeatureEngineeredPredictiveModel buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        List<AttributesEnricher> enrichers = Lists.newArrayListWithExpectedSize(enrichStrategies.size());

        for (AttributesEnrichStrategy enrichStrategy : enrichStrategies) {
            enrichers.add(enrichStrategy.build(trainingData));
        }

        final Iterable<Instance> enrichedTrainingData = Iterables.transform(trainingData, new InstanceEnricher(enrichers));

        PredictiveModel predictiveModel = wrappedPredictiveModelBuilder.buildPredictiveModel(enrichedTrainingData);

        return new FeatureEngineeredPredictiveModel(predictiveModel, enrichers);
    }
}
