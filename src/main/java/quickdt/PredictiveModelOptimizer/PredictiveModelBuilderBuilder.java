package quickdt.PredictiveModelOptimizer;

import quickdt.PredictiveModel;
import quickdt.PredictiveModelBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public interface PredictiveModelBuilderBuilder<PM extends PredictiveModel, PMB extends PredictiveModelBuilder<PM>> {
    public PredictiveModelBuilder<PM> build(Map<String, Object> parameters);
}
