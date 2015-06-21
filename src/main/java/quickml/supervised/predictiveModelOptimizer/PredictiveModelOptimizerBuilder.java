package quickml.supervised.predictiveModelOptimizer;

import com.google.common.base.Preconditions;
import quickml.data.Instance;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.LossChecker;
import quickml.supervised.crossValidation.data.TrainingDataCycler;

import java.util.Map;

public class PredictiveModelOptimizerBuilder<PM extends PredictiveModel, L, T extends InstanceWithAttributesMap<L>> {

    private Map<String, ? extends FieldValueRecommender> valuesToTest;
    private PredictiveModelBuilder modelBuilder;
    private TrainingDataCycler<T> dataCycler;
    private LossChecker<PM, T> lossChecker;
    private int iterations = 5;


    public PredictiveModelOptimizerBuilder<PM, L, T> valuesToTest(Map<String, ? extends FieldValueRecommender> valuesToTest) {
        this.valuesToTest = valuesToTest;
        return this;
    }

    public PredictiveModelOptimizerBuilder<PM, L, T> lossChecker(LossChecker<PM, T> lossChecker) {
        this.lossChecker = lossChecker;
        return this;
    }

    public PredictiveModelOptimizerBuilder<PM, L, T> dataCycler(TrainingDataCycler<T> dataCycler) {
        this.dataCycler = dataCycler;
        return this;
    }

    public PredictiveModelOptimizerBuilder<PM, L, T> modelBuilder(PredictiveModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
        return this;
    }

    public PredictiveModelOptimizerBuilder<PM, L, T> iterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    public PredictiveModelOptimizer build() {
        Preconditions.checkArgument(modelBuilder != null, "You must supply a model builder");
        Preconditions.checkArgument(dataCycler != null, "You must supply a data cycler");
        Preconditions.checkArgument(lossChecker != null, "You must supply a loss checker");
        Preconditions.checkArgument(valuesToTest != null, "You must supply a map of treeBuildContexts to check");

        return new PredictiveModelOptimizer(valuesToTest, new CrossValidator<>(modelBuilder, lossChecker, dataCycler), iterations);
    }
}
