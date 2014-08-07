package quickdt.predictiveModels.featureEngineering;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A predictive model that wraps another predictive model but modifies the input
 * Attributes based on one or more "enrichers".  This objected is created by a
 * {@link FeatureEngineeringPredictiveModelBuilder}.
 */
public class FeatureEngineeredPredictiveModel implements PredictiveModel<Object> {
    private static final long serialVersionUID = 7279329500376419142L;
    private final PredictiveModel<Object> wrappedModel;
    private final List<AttributesEnricher> attributesEnrichers;

    public FeatureEngineeredPredictiveModel(PredictiveModel<Object> wrappedModel, List<AttributesEnricher> attributesEnrichers) {
        this.wrappedModel = wrappedModel;
        this.attributesEnrichers = attributesEnrichers;
    }

    @Override
    public double getProbability(final Map<String, Serializable> attributes, final Serializable classification) {
        Map<String, Serializable> enrichedAttributes = enrichAttributes(attributes);
        return wrappedModel.getProbability(enrichedAttributes, classification);
    }

    @Override
    public Map<Serializable, Double> getProbabilitiesByClassification(final Map<String, Serializable> attributes) {
        return wrappedModel.getProbabilitiesByClassification(attributes);
    }

    private Map<String, Serializable> enrichAttributes(final Map<String, Serializable> attributes) {
        Map<String, Serializable> enrichedAttributes = attributes;
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            enrichedAttributes = attributesEnricher.apply(enrichedAttributes);
        }
        return enrichedAttributes;
    }

    @Override
    public void dump(final PrintStream printStream) {
        wrappedModel.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Map<String, Serializable> attributes) {
        return wrappedModel.getClassificationByMaxProb(enrichAttributes(attributes));
    }
}
