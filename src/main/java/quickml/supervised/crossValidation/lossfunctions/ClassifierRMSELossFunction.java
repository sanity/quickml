package quickml.supervised.crossValidation.lossfunctions;

import quickml.supervised.crossValidation.PredictionMapResults;

import static quickml.supervised.crossValidation.lossfunctions.LossFunctions.rmseLoss;

public class ClassifierRMSELossFunction extends ClassifierLossFunction {

    @Override
    public Double getLoss(PredictionMapResults results) {
        return rmseLoss(results);
    }

    @Override
    public String getName() {
        return "RMSE";
    }
}
