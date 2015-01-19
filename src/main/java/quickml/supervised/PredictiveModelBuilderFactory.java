package quickml.supervised;

import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public interface PredictiveModelBuilderFactory<INPUT, OUTPUT, PREDICTION, PM extends PredictiveModel<INPUT, OUTPUT>, PMB extends PredictiveModelBuilder<INPUT, OUTPUT, PREDICTION, PM>> {
    public Map<String, FieldValueRecommender> createefaultParametersToOptimize();
    public PMB buildBuilder(Map<String, Object> predictiveModelConfig);

}
//options for fixing:
//change PredictiveModelWithDataBuilderBuilder to not implement PredictiveModelBuilderBuilder? Not an option...need for PMO
//chang

//PredictiveModelWithDataBuilder<R, PM> not an extension of  PredictiveModelBuilder<R, PM>?
//PredictiveModelWithDataBuilder<R, PM> is an extension of UpdatablePredictiveModelBuilder<R, PM> because it implements it.
//UpdatablePredictiveModelBuilder<R, PM> is an extension of extends PredictiveModelBuilder<R, PM>.
//so it is 2 extensions away.  Why does this not work?