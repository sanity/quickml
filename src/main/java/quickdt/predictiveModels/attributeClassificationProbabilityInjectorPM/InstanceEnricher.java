package quickdt.predictiveModels.attributeClassificationProbabilityInjectorPM;

import com.google.common.base.Function;
import quickdt.data.*;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 5/20/14.
 */
public class InstanceEnricher implements Function<AbstractInstance, Instance> {

    public static final String KEY_APPEND_STRING = "-PROB";

    private final Map<String, Map<Serializable, Double>> valueProbabilitiesByAttribute;

    public InstanceEnricher(Map<String, Map<Serializable, Double>> valueProbabilitiesByAttribute) {
        this.valueProbabilitiesByAttribute = valueProbabilitiesByAttribute;
    }

    @Override
    public Instance apply(final AbstractInstance abstractInstance) {
        // TODO: Perhaps more efficient to use immutable map for attributes here
        HashMapAttributes enrichedAttributes = new HashMapAttributes();
        enrichedAttributes.putAll(abstractInstance.getAttributes());

        for (Map.Entry<String, Map<Serializable, Double>> attributeValueProbEntry : valueProbabilitiesByAttribute.entrySet()) {
            Serializable value = abstractInstance.getAttributes().get(attributeValueProbEntry.getKey());
            if (value == null) {
                value = Integer.MIN_VALUE;
            }
            Double valueProb = attributeValueProbEntry.getValue().get(value);
            if (valueProb == null) {
                valueProb = Double.MIN_VALUE;
            }
            enrichedAttributes.put(attributeValueProbEntry.getKey()+ KEY_APPEND_STRING, valueProb);

        }
        return new Instance(enrichedAttributes, abstractInstance.getClassification(), abstractInstance.getWeight());
    }
}
