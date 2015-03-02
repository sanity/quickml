package quickml.supervised.crossValidation.lossfunctions;

import quickml.supervised.crossValidation.PredictionMapResults;

import static quickml.supervised.crossValidation.lossfunctions.LossFunctions.mseLoss;

public class ClassifierMSELossFunction extends ClassifierLossFunction {

    @Override
    public Double getLoss(PredictionMapResults results) {
        return mseLoss(results);
    }

    @Override
    public String getName() {
        return "MSE";
    }
}
