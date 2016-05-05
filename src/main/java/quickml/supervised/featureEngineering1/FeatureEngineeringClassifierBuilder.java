package quickml.supervised.featureEngineering1;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.classifier.Classifier;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A PredictiveModelBuilder that attempts to
 */
public class FeatureEngineeringClassifierBuilder implements PredictiveModelBuilder<FeatureEngineeredClassifier, InstanceWithAttributesMap<?>> {
    private static final  Logger logger =  LoggerFactory.getLogger(FeatureEngineeringClassifierBuilder.class);

    private final PredictiveModelBuilder<? extends Classifier, InstanceWithAttributesMap<?>>  wrappedBuilder;
    private final List<? extends AttributesEnrichStrategy> enrichStrategies;

    public FeatureEngineeringClassifierBuilder(PredictiveModelBuilder<? extends Classifier, InstanceWithAttributesMap<?>> wrappedBuilder, List<? extends AttributesEnrichStrategy> enrichStrategies) {
        if (enrichStrategies.isEmpty()) {
            logger.warn("Won't do anything if no AttributesEnrichStrategies are provided");
        }
        this.wrappedBuilder = wrappedBuilder;
        this.enrichStrategies = enrichStrategies;
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> config) {
        wrappedBuilder.updateBuilderConfig(config);
    }

    @Override
    public FeatureEngineeredClassifier buildPredictiveModel(Iterable<InstanceWithAttributesMap<?>> trainingData) {
        List<AttributesEnricher> enrichers = Lists.newArrayListWithExpectedSize(enrichStrategies.size());

        for (AttributesEnrichStrategy enrichStrategy : enrichStrategies) {
            enrichers.add(enrichStrategy.build(trainingData));
        }

        final Iterable<InstanceWithAttributesMap<?>> enrichedTrainingData = Iterables.transform(trainingData, new InstanceEnricher(enrichers));

        PredictiveModel<AttributesMap, PredictionMap> predictiveModel = wrappedBuilder.buildPredictiveModel(enrichedTrainingData);

        return new FeatureEngineeredClassifier(predictiveModel, enrichers);
    }

}
