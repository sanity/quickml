package quickml.supervised.crossValidation.lossfunctions;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 8/29/14.
 */
public class SingleVariableRealValuedFunctionMSECVLossFunction implements LossFunction<Double, List<LabelPredictionWeight<Serializable, Double>>> {

    public Double getLoss(List<LabelPredictionWeight<Serializable, Double>> labelPredictionWeights) {
        double totalLoss = 0;
        double weightOfAllInstances = 0;
        for (LabelPredictionWeight<Serializable, Double> labelPredictionWeight : labelPredictionWeights) {
            double prediction = labelPredictionWeight.getPrediction();
            double trueValue = (Double) labelPredictionWeight.getLabel();
            totalLoss += (prediction - trueValue) * (prediction - trueValue) * labelPredictionWeight.getWeight();
            weightOfAllInstances += labelPredictionWeight.getWeight();
        }
        return weightOfAllInstances > 0 ? totalLoss / weightOfAllInstances : 0;
    }

    @Override
    public String getName() {
        return "SingleVariableRealValuedFunctionMSECVLossFunction";
    }
}

