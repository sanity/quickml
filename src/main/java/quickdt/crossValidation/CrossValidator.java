package quickdt.crossValidation;

import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public abstract class CrossValidator<R, P> {
    public abstract <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R, PM> predictiveModelBuilder, Iterable<? extends Instance<R>> allTrainingData);
}