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
//options for fixing:
//change PredictiveModelWithDataBuilderBuilder to not implement PredictiveModelBuilderBuilder? Not an option...need for PMO
//chang

//PredictiveModelWithDataBuilder<R, PM> not an extension of  PredictiveModelBuilder<R, PM>?
//PredictiveModelWithDataBuilder<R, PM> is an extension of UpdatablePredictiveModelBuilder<R, PM> because it implements it.
//UpdatablePredictiveModelBuilder<R, PM> is an extension of extends PredictiveModelBuilder<R, PM>.
//so it is 2 extensions away.  Why does this not work?