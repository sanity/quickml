package quickml.supervised;

import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * A supervised learning algorithm, which, given data, will generate a PredictiveModel.
 */
public interface PredictiveModelBuilder<PM extends PredictiveModel, I extends Instance> {

    public PM buildPredictiveModel(Iterable<I> trainingData);

    public void updateBuilderConfig(Map<String, Serializable> config);
}
