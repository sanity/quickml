package quickml.supervised.featureEngineering;

import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 5/21/14.
 */
public interface AttributesEnrichStrategy {
    public AttributesEnricher build(Iterable<? extends Instance<AttributesMap, Serializable>> trainingData);
}
