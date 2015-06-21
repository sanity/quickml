package quickml.supervised;

import quickml.data.Instance;

import java.util.Map;

/**
 * A supervised learning algorithm, which, given data, will generate a PredictiveModel.
 */
public interface PredictiveModelBuilder<P, A, PM extends PredictiveModel<A, P>, L, I extends Instance<A, L>> {

    public PM buildPredictiveModel(Iterable<I> trainingData);

    public void updateBuilderConfig(Map<String, Object> config);
}
