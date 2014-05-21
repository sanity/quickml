package quickdt.predictiveModels.featureEngineering;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by ian on 5/20/14.
 */
public class FeatureEngineeredPredictiveModel implements PredictiveModel {
    private static final long serialVersionUID = 7279329500376419142L;
    private final PredictiveModel wrappedModel;
    private final List<AttributesEnricher> attributesEnrichers;

    public FeatureEngineeredPredictiveModel(PredictiveModel wrappedModel, List<AttributesEnricher> attributesEnrichers) {
        this.wrappedModel = wrappedModel;
        this.attributesEnrichers = attributesEnrichers;
    }


    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        Attributes enrichedAttributes = enrichAttributes(attributes);
        return wrappedModel.getProbability(enrichedAttributes, classification);
    }

    private Attributes enrichAttributes(final Attributes attributes) {
        Attributes enrichedAttributes = attributes;
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
    public Serializable getClassificationByMaxProb(final Attributes attributes) {
        return wrappedModel.getClassificationByMaxProb(enrichAttributes(attributes));
    }
}
