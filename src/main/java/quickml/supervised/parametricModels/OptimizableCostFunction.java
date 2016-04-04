package quickml.supervised.parametricModels;

import quickml.data.instances.Instance;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/1/16.
 */
public interface OptimizableCostFunction<T extends Instance> {
    double computeCost(List<? extends T> instances, double[] weights, double minPredictedProbablity);
    void updateGradient(final List<? extends T> instances, final double[] fixedWeights, double[] gradient);
    public void updateBuilderConfig(final Map<String, Serializable> config);
    public void shutdown();
}