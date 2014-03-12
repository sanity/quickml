package quickdt;

import quickdt.PredictiveModel;
import quickdt.PredictiveModelBuilder;
import quickdt.PredictiveModelOptimizer.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public interface PredictiveModelBuilderBuilder<PM extends PredictiveModel, PMB extends PredictiveModelBuilder<PM>> {
    public List<Parameter> createDefaultParameters();
    public HashMap<String, Object> createPredictiveModelConfig(List<Parameter> parameters);
    public PredictiveModelBuilder<PM> buildBuilder(Map<String, Object> predictiveModelConfig);

}
