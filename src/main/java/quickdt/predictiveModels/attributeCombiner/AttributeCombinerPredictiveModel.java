package quickdt.predictiveModels.attributeCombiner;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Created by ian on 3/28/14.
 */
public class AttributeCombinerPredictiveModel implements PredictiveModel {
    private static final long serialVersionUID = 5365821854303467792L;
    public final PredictiveModel predictiveModel;
    private final AttributeEnricher attributeEnricher;

    public AttributeCombinerPredictiveModel(final PredictiveModel predictiveModel, final AttributeEnricher attributeEnricher) {
        this.predictiveModel = predictiveModel;
        this.attributeEnricher = attributeEnricher;
    }

    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        return predictiveModel.getProbability(attributeEnricher.apply(attributes), classification);
    }

    @Override
    public void dump(final PrintStream printStream) {
        predictiveModel.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Attributes attributes) {
        return predictiveModel.getClassificationByMaxProb(attributeEnricher.apply(attributes));
    }
}
