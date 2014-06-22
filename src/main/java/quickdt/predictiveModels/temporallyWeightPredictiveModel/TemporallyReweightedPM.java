package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.splitOnAttributePredictiveModel.SplitOnAttributePMBuilder;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 6/20/14.
 */
public class TemporallyReweightedPM implements PredictiveModel{
    private static final long serialVersionUID = 2642074639257374588L;
    private final PredictiveModel wrappedModel;

    public TemporallyReweightedPM(PredictiveModel predictiveModel) {
        this.wrappedModel = predictiveModel;
    }

    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        return wrappedModel.getProbability(attributes, classification);
    }

    @Override
    public Map<Serializable, Double> getProbabilitiesByClassification(final Attributes attributes) {
        return wrappedModel.getProbabilitiesByClassification(attributes);
    }

    @Override
    public void dump(final PrintStream printStream) {
        wrappedModel.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Attributes attributes) {
        return wrappedModel.getClassificationByMaxProb(attributes);
    }

    public PredictiveModel getWrappedModel() {
        return wrappedModel;
    }

}
