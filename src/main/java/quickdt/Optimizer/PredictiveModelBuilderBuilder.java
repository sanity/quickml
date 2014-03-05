package quickdt.Optimizer;

import quickdt.PredictiveModel;
import quickdt.PredictiveModelBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public interface PredictiveModelBuilderBuilder<PM extends PredictiveModel, PMB extends PredictiveModelBuilder<PM>> {
    //returns a predictiveModelBuilder which is ready to call build() to return a predictive model.
    public PredictiveModelBuilder<PM> build(Map<String, Object> parameters);
}
