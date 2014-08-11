package quickdt.predictiveModels.featureEngineering;

import quickdt.predictiveModels.Classifier;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A predictive model that wraps another predictive model but modifies the input
 * Attributes based on one or more "enrichers".  This objected is created by a
 * {@link FeatureEngineeringPredictiveModelBuilder}.
 */
public class FeatureEngineeredPredictiveModel extends Classifier {
    private static final long serialVersionUID = 7279329500376419142L;
    private final Classifier wrappedClassifier;
    private final List<AttributesEnricher> attributesEnrichers;

    public FeatureEngineeredPredictiveModel(Classifier wrappedClassifier, List<AttributesEnricher> attributesEnrichers) {
        this.wrappedClassifier = wrappedClassifier;
        this.attributesEnrichers = attributesEnrichers;
    }

    @Override
    public Map<Serializable, Double> predict(Map<String, Serializable> attributes) {
        Map<String, Serializable> enrichedAttributes = enrichAttributes(attributes);
        return wrappedClassifier.predict(enrichedAttributes);
    }

    private Map<String, Serializable> enrichAttributes(final Map<String, Serializable> attributes) {
        Map<String, Serializable> enrichedAttributes = attributes;
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            enrichedAttributes = attributesEnricher.apply(enrichedAttributes);
        }
        return enrichedAttributes;
    }

    @Override
    public void dump(final Appendable appendable) {
        wrappedClassifier.dump(appendable);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Map<String, Serializable> attributes) {
        return wrappedClassifier.getClassificationByMaxProb(enrichAttributes(attributes));
    }
}
