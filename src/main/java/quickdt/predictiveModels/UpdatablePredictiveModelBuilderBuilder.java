package quickdt.predictiveModels;

import java.util.Map;

/**
 * Created by chrisreeves on 7/2/14.
 */
public interface UpdatablePredictiveModelBuilderBuilder<PM extends PredictiveModel, PMB extends UpdatablePredictiveModelBuilder<PM>> extends PredictiveModelBuilderBuilder<PM,PMB> {
    public PMB buildBuilder(Map<String, Object> predictiveModelConfig);
}
