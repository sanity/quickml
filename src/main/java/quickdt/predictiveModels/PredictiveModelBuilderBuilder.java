package quickdt.predictiveModels;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public interface PredictiveModelBuilderBuilder<PM extends PredictiveModel, PMB extends PredictiveModelBuilder<PM>> {
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize();
    public PMB buildBuilder(Map<String, Object> predictiveModelConfig);

}
