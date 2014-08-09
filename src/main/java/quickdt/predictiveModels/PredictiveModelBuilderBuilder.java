package quickdt.predictiveModels;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public interface PredictiveModelBuilderBuilder<R, PM extends PredictiveModel<R, ?>, PMB extends PredictiveModelBuilder<R, PM>> {
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize();
    public PMB buildBuilder(Map<String, Object> predictiveModelConfig);

}
