package quickml.supervised.featureEngineering;

import quickml.data.InstanceWithAttributesMap;

/**
 * Created by ian on 5/21/14.
 */
public interface AttributesEnrichStrategy {
    public AttributesEnricher build(Iterable<InstanceWithAttributesMap> trainingData);
}
