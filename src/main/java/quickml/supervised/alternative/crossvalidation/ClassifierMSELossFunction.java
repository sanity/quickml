package quickml.supervised.alternative.crossvalidation;

import static quickml.supervised.alternative.crossvalidation.LossFunctions.mseLoss;

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
