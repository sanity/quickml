package quickdt.crossValidation;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public abstract class CrossValidator<T extends PredictiveModel> {
    public abstract double getCrossValidatedLoss(PredictiveModelBuilder<T> predictiveModelBuilder, Iterable<? extends AbstractInstance> allTrainingData);
}