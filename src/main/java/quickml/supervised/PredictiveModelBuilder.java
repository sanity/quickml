package quickml.supervised;

import quickml.data.Instance;

import java.util.Map;

/**
 * A supervised learning algorithm, which, given data, will generate a PredictiveModel.
 */
public interface PredictiveModelBuilder<A, PM extends PredictiveModel<A, ?>, I extends Instance<A, ?>> {

    public PM buildPredictiveModel(Iterable<I> trainingData);

    public void updateBuilderConfig(Map<String, Object> config);
}
