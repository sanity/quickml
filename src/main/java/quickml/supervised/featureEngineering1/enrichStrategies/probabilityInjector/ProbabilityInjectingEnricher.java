package quickml.supervised.featureEngineering1.enrichStrategies.probabilityInjector;

import quickml.data.AttributesMap;
import quickml.supervised.featureEngineering1.AttributesEnricher;

import java.io.Serializable;
import java.util.Map;

/**
 * See {@link quickml.supervised.featureEngineering1.enrichStrategies.probabilityInjector.ProbabilityEnrichStrategy}
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
    public AttributesMap apply(final AttributesMap attributes) {
        // TODO: Perhaps more efficient to use immutable map for attributes here
        AttributesMap enrichedAttributes = AttributesMap.newHashMap();
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
