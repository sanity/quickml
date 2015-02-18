package quickml.supervised.featureEngineering;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.Classifier;

import java.util.List;
import java.util.Map;

/**
 * A PredictiveModelBuilder that attempts to
 */
public class FeatureEngineeringPredictiveModelBuilder implements PredictiveModelBuilder<FeatureEngineeredPredictiveModel, ClassifierInstance> {
    private static final  Logger logger =  LoggerFactory.getLogger(FeatureEngineeringPredictiveModelBuilder.class);

    private final PredictiveModelBuilder<Classifier, ClassifierInstance>  wrappedBuilder;
    private final List<? extends AttributesEnrichStrategy> enrichStrategies;

    public FeatureEngineeringPredictiveModelBuilder(PredictiveModelBuilder<Classifier, ClassifierInstance>  wrappedBuilder, List<? extends AttributesEnrichStrategy> enrichStrategies) {
        if (enrichStrategies.isEmpty()) {
            logger.warn("Won't do anything if no AttributesEnrichStrategies are provided");
        }
        this.wrappedBuilder = wrappedBuilder;
        this.enrichStrategies = enrichStrategies;
    }

    @Override
    public void updateBuilderConfig(Map<String, Object> config) {
        wrappedBuilder.updateBuilderConfig(config);
    }

    @Override
    public FeatureEngineeredPredictiveModel buildPredictiveModel(Iterable<ClassifierInstance> trainingData) {
        List<AttributesEnricher> enrichers = Lists.newArrayListWithExpectedSize(enrichStrategies.size());

        for (AttributesEnrichStrategy enrichStrategy : enrichStrategies) {
            enrichers.add(enrichStrategy.build(trainingData));
        }

        final Iterable<ClassifierInstance> enrichedTrainingData = Iterables.transform(trainingData, new InstanceEnricher(enrichers));

        PredictiveModel<AttributesMap, PredictionMap> predictiveModel = wrappedBuilder.buildPredictiveModel(enrichedTrainingData);

        return new FeatureEngineeredPredictiveModel(predictiveModel, enrichers);
    }

}
