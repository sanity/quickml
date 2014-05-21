package quickdt.predictiveModels.featureEngineering.enrichStrategies.probabilityInjector;

import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.predictiveModels.featureEngineering.AttributesEnricher;

import java.io.Serializable;
import java.util.Map;

/**
 * See {@link quickdt.predictiveModels.featureEngineering.enrichStrategies.probabilityInjector.ProbabilityEnrichStrategy}
 */
public class ProbabilityInjectingEnricher implements AttributesEnricher {
    public static final String KEY_APPEND_STRING = "-PROB";

    private static final long serialVersionUID = 3716323873913862361L;
    private static final int MISSING_VALUE_PLACEHOLDER = Integer.MIN_VALUE;
    private static final double MISSING_PROBABILITY_PLACEHOLDER = Double.MIN_VALUE;

    private final Map<String, Map<Serializable, Double>> valueProbabilitiesByAttribute;

    public ProbabilityInjectingEnricher(Map<String, Map<Serializable, Double>> valueProbabilitiesByAttribute) {
        this.valueProbabilitiesByAttribute = valueProbabilitiesByAttribute;
    }

    @Override
    public Attributes apply(final Attributes attributes) {
        // TODO: Perhaps more efficient to use immutable map for attributes here
        HashMapAttributes enrichedAttributes = new HashMapAttributes();
        enrichedAttributes.putAll(attributes);

        for (Map.Entry<String, Map<Serializable, Double>> attributeValueProbEntry : valueProbabilitiesByAttribute.entrySet()) {
            Serializable value = attributes.get(attributeValueProbEntry.getKey());
            if (value == null) {
                value = MISSING_VALUE_PLACEHOLDER;
            }
            Double valueProb = attributeValueProbEntry.getValue().get(value);
            if (valueProb == null) {
                valueProb = MISSING_PROBABILITY_PLACEHOLDER;
            }
            enrichedAttributes.put(attributeValueProbEntry.getKey()+ KEY_APPEND_STRING, valueProb);

        }
        return enrichedAttributes;
    }
}
