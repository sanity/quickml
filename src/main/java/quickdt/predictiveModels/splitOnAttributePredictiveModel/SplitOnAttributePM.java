package quickdt.predictiveModels.splitOnAttributePredictiveModel;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 5/29/14.
 */
public class SplitOnAttributePM implements PredictiveModel {
    private static final long serialVersionUID = 2642074639257374588L;
    private final String attributeKey;
    private final Map<Serializable, PredictiveModel> splitModels;
    private final PredictiveModel defaultPM;

    public SplitOnAttributePM(String attributeKey, final Map<Serializable, PredictiveModel> splitModels, PredictiveModel defaultPM) {
        this.attributeKey = attributeKey;
        this.splitModels = splitModels;
        this.defaultPM = defaultPM;
    }

    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        return getModelForAttributes(attributes).getProbability(attributes, classification);
    }

    @Override
    public Map<Serializable, Double> getProbabilitiesByClassification(final Attributes attributes) {
        return getModelForAttributes(attributes).getProbabilitiesByClassification(attributes);
    }

    @Override
    public void dump(final PrintStream printStream) {
        for (Map.Entry<Serializable, PredictiveModel> splitModelEntry : splitModels.entrySet()) {
            printStream.println("Predictive model for "+attributeKey+"="+splitModelEntry.getKey());
            splitModelEntry.getValue().dump(printStream);
        }
        printStream.println("Default");
        defaultPM.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Attributes attributes) {
        return getModelForAttributes(attributes).getClassificationByMaxProb(attributes);
    }

    public PredictiveModel getDefaultPM() {
        return defaultPM;
    }

    public Map<Serializable, PredictiveModel> getSplitModels() {
        return splitModels;
    }

    private PredictiveModel getModelForAttributes(Attributes attributes) {
        Serializable value = attributes.get(attributeKey);
        if (value == null) value = SplitOnAttributePMBuilder.NO_VALUE_PLACEHOLDER;
        PredictiveModel predictiveModel = splitModels.get(value);
        if (predictiveModel == null) {
            predictiveModel = defaultPM;
        }
        return predictiveModel;
    }

}
