package quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions;

import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.supervised.crossValidation.lossfunctions.LossFunction;

public abstract class ClassifierLossFunction implements LossFunction<PredictionMapResults> {

    public abstract Double getLoss(PredictionMapResults results);

    public abstract String getName();

}
