package quickml.supervised;

import java.util.Map;

/**
 * Created by chrisreeves on 7/2/14.
 */
public interface UpdatablePredictiveModelBuilderFactory<R, PM extends PredictiveModel<R, ?>, PMB extends UpdatablePredictiveModelBuilder<R, PM>> extends PredictiveModelBuilderFactory<R, PM,PMB> {
    public PMB buildBuilder(Map<String, Object> predictiveModelConfig);
}
