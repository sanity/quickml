package quickdt.predictiveModels.attributeClassificationProbabilityInjectorPM;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 5/19/14.
 */
public class AttributeClassProbInjectorPM implements PredictiveModel {
    private static final long serialVersionUID = -4780482765256947163L;
    private final Map<String, Map<Serializable, Double>> attributeValueProbabilitiesByAttribute;

    public AttributeClassProbInjectorPM(PredictiveModel wrappedPM, final Map<String, Map<Serializable, Double>> attributeValueProbabilitiesByAttribute) {
        this.attributeValueProbabilitiesByAttribute = attributeValueProbabilitiesByAttribute;
    }

    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        return 0;
    }

    @Override
    public void dump(final PrintStream printStream) {

    }

    @Override
    public Serializable getClassificationByMaxProb(final Attributes attributes) {
        return null;
    }
}
