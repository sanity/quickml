package quickdt.predictiveModels;

import quickdt.predictiveModelOptimizer.ParameterToOptimize;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public interface PredictiveModelBuilderBuilder<PM extends PredictiveModel, PMB extends PredictiveModelBuilder<PM>> {
    public List<ParameterToOptimize> createDefaultParametersToOptimize();
    public Map<String, Object> createPredictiveModelConfig(List<ParameterToOptimize> parameters);
    public PredictiveModelBuilder<PM> buildBuilder(Map<String, Object> predictiveModelConfig);

}
