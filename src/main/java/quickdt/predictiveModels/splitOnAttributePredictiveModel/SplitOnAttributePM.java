package quickdt.predictiveModels.splitOnAttributePredictiveModel;

import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.predictiveModels.Classifier;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 5/29/14.
 */
public class SplitOnAttributePM implements Classifier {
    private static final long serialVersionUID = 2642074639257374588L;
    private final String attributeKey;
    private final Map<Serializable, Classifier> splitModels;
    private final Classifier defaultPM;

    public SplitOnAttributePM(String attributeKey, final Map<Serializable, Classifier> splitModels, Classifier defaultPM) {
        this.attributeKey = attributeKey;
        this.splitModels = splitModels;
        this.defaultPM = defaultPM;
    }

    @Override
    public Double predict(AbstractInstance instance) {
        return getProbability(instance.getRegressors(), instance.getLabel());
    }

    @Override
    public double getProbability(final Map<String, Serializable> attributes, final Serializable classification) {
        return getModelForAttributes(attributes).getProbability(attributes, classification);
    }

    @Override
    public Map<Serializable, Double> predict(final Map<String, Serializable> attributes) {
        return getModelForAttributes(attributes).predict(attributes);
    }

    @Override
    public void dump(final PrintStream printStream) {
        for (Map.Entry<Serializable, Classifier> splitModelEntry : splitModels.entrySet()) {
            printStream.println("Predictive model for "+attributeKey+"="+splitModelEntry.getKey());
            splitModelEntry.getValue().dump(printStream);
        }
        printStream.println("Default");
        defaultPM.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Map<String, Serializable> attributes) {
        return getModelForAttributes(attributes).getClassificationByMaxProb(attributes);
    }

    public Classifier getDefaultPM() {
        return defaultPM;
    }

    public Map<Serializable, Classifier> getSplitModels() {
        return splitModels;
    }

    private Classifier getModelForAttributes(Map<String, Serializable> attributes) {
        Serializable value = attributes.get(attributeKey);
        if (value == null) value = SplitOnAttributePMBuilder.NO_VALUE_PLACEHOLDER;
        Classifier predictiveModel = splitModels.get(value);
        if (predictiveModel == null) {
            predictiveModel = defaultPM;
        }
        return predictiveModel;
    }

}
