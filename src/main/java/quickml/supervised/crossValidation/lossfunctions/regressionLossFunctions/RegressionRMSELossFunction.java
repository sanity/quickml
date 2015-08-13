package quickml.supervised.crossValidation.lossfunctions.regressionLossFunctions;

import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;

import java.util.List;

import static quickml.supervised.crossValidation.lossfunctions.LossFunctions.rmseRegressionLoss;

public class RegressionRMSELossFunction extends RegressionLossFunction {

    @Override
    public Double getLoss(List<LabelPredictionWeight<Double, Double>> results) {
        return rmseRegressionLoss(results);
    }

    @Override
    public String getName() {
        return "RMSE";
    }
}
