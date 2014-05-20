package quickdt.predictiveModels.featureEngineering;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Created by ian on 5/20/14.
 */
public class FeatureEngineeredPredictiveModel implements PredictiveModel {
    private static final long serialVersionUID = 7279329500376419142L;
    private final PredictiveModel wrappedModel;
    private final AttributesEnricher attributesEnricher;

    public FeatureEngineeredPredictiveModel(PredictiveModel wrappedModel, AttributesEnricher attributesEnricher) {
        this.wrappedModel = wrappedModel;
        this.attributesEnricher = attributesEnricher;
    }


    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        return wrappedModel.getProbability(attributesEnricher.apply(attributes), classification);
    }

    @Override
    public void dump(final PrintStream printStream) {
        wrappedModel.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Attributes attributes) {
        return wrappedModel.getClassificationByMaxProb(attributesEnricher.apply(attributes));
    }
}
