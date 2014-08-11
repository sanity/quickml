package quickdt.predictiveModels.featureEngineering;

import quickdt.data.Instance;

/**
 * Created by ian on 5/21/14.
 */
public interface AttributesEnrichStrategy {
    public AttributesEnricher build(Iterable<? extends Instance> trainingData);
}
