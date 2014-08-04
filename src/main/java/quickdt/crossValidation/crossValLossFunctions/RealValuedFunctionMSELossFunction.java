package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.RealValuedFunction;
import quickdt.predictiveModels.RealValuedFunctionPrediction;

import java.util.List;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class RealValuedFunctionMSELossFunction implements CrossValLossFunction<RealValuedFunctionPrediction> {
    double totalLoss = 0;
    double weightOfAllInstances = 0;

    @Override
    public double getLoss(List<LabelPredictionWeight<RealValuedFunctionPrediction>> labelPredictionWeights) {
        for (LabelPredictionWeight<RealValuedFunctionPrediction> labelPredictionWeight : labelPredictionWeights) {
            double predictedValueOfInstance = labelPredictionWeight.getPrediction().getPrediction();
            totalLoss += Math.pow((Double)labelPredictionWeight.getLabel() - predictedValueOfInstance, 2) * labelPredictionWeight.getWeight();
            weightOfAllInstances += labelPredictionWeight.getWeight();
        }
        return totalLoss / weightOfAllInstances;
    }
}

