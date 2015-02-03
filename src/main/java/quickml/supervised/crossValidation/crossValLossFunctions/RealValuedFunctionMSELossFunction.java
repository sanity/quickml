package quickml.supervised.crossValidation.crossValLossFunctions;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class RealValuedFunctionMSELossFunction implements CrossValLossFunction<Serializable, Double> {
    double totalLoss = 0;
    double weightOfAllInstances = 0;

    @Override
    public double getLoss(List<LabelPredictionWeight<Serializable, Double>> labelPredictionWeights) {
        for (LabelPredictionWeight<Serializable, Double> labelPredictionWeight : labelPredictionWeights) {
            double predictedValueOfInstance = labelPredictionWeight.getPrediction();
            totalLoss += Math.pow((Double)labelPredictionWeight.getLabel() - predictedValueOfInstance, 2) * labelPredictionWeight.getWeight();
            weightOfAllInstances += labelPredictionWeight.getWeight();
        }
        return totalLoss / weightOfAllInstances;
    }
}

