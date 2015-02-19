package quickml.supervised.featureEngineering;

import quickml.data.ClassifierInstance;

/**
 * Created by ian on 5/21/14.
 */
public interface AttributesEnrichStrategy {
    public AttributesEnricher build(Iterable<ClassifierInstance> trainingData);
}
