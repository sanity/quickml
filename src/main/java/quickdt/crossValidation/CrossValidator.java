package quickdt.crossValidation;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Prediction;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public abstract class CrossValidator<Pr> {
    public abstract <PM extends PredictiveModel<Pr>> double getCrossValidatedLoss(PredictiveModelBuilder<PM> predictiveModelBuilder, Iterable<? extends AbstractInstance> allTrainingData);
}