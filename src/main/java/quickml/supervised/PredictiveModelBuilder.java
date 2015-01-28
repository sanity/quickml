package quickml.supervised;

import quickml.data.Instance;

import java.util.Map;

/**
 * A supervised learning algorithm, which, given data, will generate a PredictiveModel.
 */
public interface PredictiveModelBuilder<P extends PredictiveModel, T extends Instance> {

    public P buildPredictiveModel(Iterable<T> trainingData);

    public void updateBuilderConfig(Map<String, Object> config);
}
