package quickml.supervised.classifier.splitOnAttribute;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.Classifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 5/29/14.
 */
public class SplitOnAttributeClassifier extends AbstractClassifier {
    private static final long serialVersionUID = 2642074639257374588L;
    private final String attributeKey;
    private final Map<Serializable, Classifier> splitModels;
    private final Classifier defaultPM;

    public SplitOnAttributeClassifier(String attributeKey, final Map<Serializable, Classifier> splitModels, Classifier defaultPM) {
        this.attributeKey = attributeKey;
        this.splitModels = splitModels;
        this.defaultPM = defaultPM;
    }

    @Override
    public double getProbability(final AttributesMap attributes, final Serializable classification) {
        return getModelForAttributes(attributes).getProbability(attributes, classification);
    }

    @Override
    public PredictionMap predict(final AttributesMap attributes) {
        return getModelForAttributes(attributes).predict(attributes);
    }

    @Override
    public void dump(final Appendable appendable) {
        try {
            for (Map.Entry<Serializable, Classifier> splitModelEntry : splitModels.entrySet()) {
                appendable.append("Predictive model for " + attributeKey + "=" + splitModelEntry.getKey());
                splitModelEntry.getValue().dump(appendable);
            }
            appendable.append("Default");
            defaultPM.dump(appendable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Serializable getClassificationByMaxProb(final AttributesMap attributes) {
        return getModelForAttributes(attributes).getClassificationByMaxProb(attributes);
    }

    public Classifier getDefaultPM() {
        return defaultPM;
    }

    public Map<Serializable, Classifier> getSplitModels() {
        return splitModels;
    }

    private Classifier getModelForAttributes(AttributesMap attributes) {
        Serializable value = attributes.get(attributeKey);
        if (value == null) value = SplitOnAttributeClassifierBuilder.NO_VALUE_PLACEHOLDER;
        Classifier predictiveModel = splitModels.get(value);
        if (predictiveModel == null) {
            predictiveModel = defaultPM;
        }
        return predictiveModel;
    }

}
