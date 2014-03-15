package quickdt;

import quickdt.predictiveModelOptimizer.Parameter;

import java.util.*;

/**
 * Created by alexanderhawk on 3/4/14.
 */
public interface PredictiveModelBuilderBuilder<PM extends PredictiveModel, PMB extends PredictiveModelBuilder<PM>> {
    public List<Parameter> createDefaultParameters();
    public HashMap<String, Object> createPredictiveModelConfig(List<Parameter> parameters);
    public PredictiveModelBuilder<PM> buildBuilder(Map<String, Object> predictiveModelConfig);

}
