package quickml.supervised.crossValidation;

import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public abstract class CrossValidator<R, P> {
    public abstract <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R, PM> predictiveModelBuilder, Iterable<Instance<R>> allTrainingData);
}