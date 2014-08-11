package quickdt.predictiveModels.featureEngineering.enrichStrategies.attributeCombiner;

import quickdt.data.Instance;
import quickdt.predictiveModels.featureEngineering.AttributesEnrichStrategy;
import quickdt.predictiveModels.featureEngineering.AttributesEnricher;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
    public AttributesEnricher build(final Iterable<Instance<Map<String,Serializable>>> trainingData) {
        return new AttributeCombiningEnricher(attributesToCombine);
    }
}
