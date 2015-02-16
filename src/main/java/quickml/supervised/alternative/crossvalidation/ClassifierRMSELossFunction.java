package quickml.supervised.alternative.crossvalidation;

import static quickml.supervised.alternative.crossvalidation.LossFunctions.rmseLoss;

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
