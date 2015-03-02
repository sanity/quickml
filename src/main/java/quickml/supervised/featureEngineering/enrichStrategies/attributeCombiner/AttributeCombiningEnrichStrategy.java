package quickml.supervised.featureEngineering.enrichStrategies.attributeCombiner;

import quickml.data.ClassifierInstance;
import quickml.supervised.featureEngineering.AttributesEnrichStrategy;
import quickml.supervised.featureEngineering.AttributesEnricher;

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
    public AttributesEnricher build(final Iterable<ClassifierInstance> trainingData) {
        return new AttributeCombiningEnricher(attributesToCombine);
    }
}
