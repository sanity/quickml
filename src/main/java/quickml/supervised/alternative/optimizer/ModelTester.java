package quickml.supervised.alternative.optimizer;

import quickml.data.Instance;
import quickml.supervised.PredictiveModelBuilder;

import java.util.List;
import java.util.Map;

public class ModelTester {

    public ModelTester(CrossValidator2 crossValidator, List<Instance> trainingInstances, PredictiveModelBuilder modelBuilder) {
        crossValidator.getCrossValidatedLoss();
    }

    public double testModel(Map<String, Object> config) {
        return 0.0d;


    }
}
