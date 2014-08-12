package quickml.supervised.featureEngineering;

import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 5/21/14.
 */
public interface AttributesEnrichStrategy {
    public AttributesEnricher build(Iterable<Instance<Map<String,Serializable>>> trainingData);
}
