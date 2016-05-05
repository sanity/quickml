package quickml.supervised.featureEngineering1.enrichStrategies.attributeCombiner;

import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.featureEngineering1.AttributesEnrichStrategy;
import quickml.supervised.featureEngineering1.AttributesEnricher;

import java.util.List;
import java.util.Set;

/**
 * An AttributesEnrichStrategy that takes several lists of attribute keys, and combines
 * the values of each of those attributes into a new attribute.
 */
public class AttributeCombiningEnrichStrategy implements AttributesEnrichStrategy {
    private final Set<List<String>> attributesToCombine;

    public AttributeCombiningEnrichStrategy(final Set<List<String>> attributesToCombine) {
        this.attributesToCombine = attributesToCombine;
    }

    @Override
    public AttributesEnricher build(final Iterable<InstanceWithAttributesMap<?>> trainingData) {
        return new AttributeCombiningEnricher(attributesToCombine);
    }
}
