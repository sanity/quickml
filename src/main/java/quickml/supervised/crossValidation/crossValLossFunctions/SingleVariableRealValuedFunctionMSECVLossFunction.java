package quickml.supervised.crossValidation.crossValLossFunctions;

import quickml.data.PredictionMap;

import java.util.List;

/**
 * Created by alexanderhawk on 8/29/14.
 */
public class SingleVariableRealValuedFunctionMSECVLossFunction implements CrossValLossFunction<Double> { // <CP extends ClassifierPrediction> implements CrossValLossFunction<CP> {
    protected double totalLoss = 0;
    double weightOfAllInstances = 0;

    public double getLoss(List<LabelPredictionWeight<Double>> labelPredictionWeights) {
        totalLoss = 0;
        weightOfAllInstances = 0;
        for (LabelPredictionWeight<Double> labelPredictionWeight : labelPredictionWeights) {
            double prediction = labelPredictionWeight.getPrediction();
            double trueValue = (Double) labelPredictionWeight.getLabel();
            totalLoss += (prediction - trueValue) * (prediction - trueValue) * labelPredictionWeight.getWeight();
            weightOfAllInstances += labelPredictionWeight.getWeight();
        }
        return weightOfAllInstances > 0 ? totalLoss / weightOfAllInstances : 0;
    }
}

