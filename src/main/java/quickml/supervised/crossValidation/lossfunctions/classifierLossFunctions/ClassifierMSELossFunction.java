package quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions;

import quickml.supervised.crossValidation.PredictionMapResults;

import static quickml.supervised.crossValidation.lossfunctions.LossFunctions.mseClassifierLoss;

public class ClassifierMSELossFunction extends ClassifierLossFunction {

    @Override
    public Double getLoss(PredictionMapResults results) {
        return mseClassifierLoss(results);
    }

    @Override
    public String getName() {
        return "MSE";
    }
}
