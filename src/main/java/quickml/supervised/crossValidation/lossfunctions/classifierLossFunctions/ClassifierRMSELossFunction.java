package quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions;

import quickml.supervised.crossValidation.PredictionMapResults;

import static quickml.supervised.crossValidation.lossfunctions.LossFunctions.rmseClassifierLoss;

public class ClassifierRMSELossFunction extends ClassifierLossFunction {

    @Override
    public Double getLoss(PredictionMapResults results) {
        return rmseClassifierLoss(results);
    }

    @Override
    public String getName() {
        return "RMSE";
    }
}
