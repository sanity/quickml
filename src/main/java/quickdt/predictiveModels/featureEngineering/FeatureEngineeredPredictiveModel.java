package quickdt.predictiveModels.featureEngineering;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.wrappedPredictiveModel.WrappedPredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

/**
 * A predictive model that wraps another predictive model but modifies the input
 * Attributes based on one or more "enrichers".  This objected is created by a
 * {@link FeatureEngineeringPredictiveModelBuilder}.
 */
public class FeatureEngineeredPredictiveModel extends WrappedPredictiveModel {
    private static final long serialVersionUID = 7279329500376419142L;
    private final List<AttributesEnricher> attributesEnrichers;

    public FeatureEngineeredPredictiveModel(PredictiveModel wrappedModel, List<AttributesEnricher> attributesEnrichers) {
        super(wrappedModel);
        this.attributesEnrichers = attributesEnrichers;
    }

    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        Attributes enrichedAttributes = enrichAttributes(attributes);
        return predictiveModel.getProbability(enrichedAttributes, classification);
    }

    private Attributes enrichAttributes(final Attributes attributes) {
        Attributes enrichedAttributes = attributes;
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            enrichedAttributes = attributesEnricher.apply(enrichedAttributes);
        }
        return enrichedAttributes;
    }
}
