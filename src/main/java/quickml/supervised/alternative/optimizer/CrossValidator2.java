package quickml.supervised.alternative.optimizer;

import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;

import java.util.List;

public class CrossValidator2<PM extends PredictiveModel, T extends Instance> {


    private OutOfTimeData<T> outOfTimeData;

    public CrossValidator2(OutOfTimeData<T> outOfTimeData, PredictiveModelBuilder<PM, T> modelBuilder) {
        this.outOfTimeData = outOfTimeData;
    }

    public double getCrossValidatedLoss() {
        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        while (outOfTimeData.hasMore()) {


            List<T> trainingSet = outOfTimeData.getTrainingSet();



            // Model Builder

            // Predicitive Model

            // CrossValidation

            // Label Prediction Weights

            // Cross Validation Loss Function - Takes in the labels, weights and predictions - returns loss

            //


//            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingDataToAddToPredictiveModel);
//            List<LabelPredictionWeight<L,P>> labelPredictionWeights;
//            labelPredictionWeights = Utils.createLabelPredictionWeights(outOfTimeData.getValidationSet(), predictiveModel);
//            runningLoss += crossValLossFunction.getLoss(labelPredictionWeights) * weightOfValidationSet;
//            runningWeightOfValidationSet += weightOfValidationSet;
//            outOfTimeData.nextValidationSet();
        }
        final double averageLoss = runningLoss / runningWeightOfValidationSet;
//        logger.info("Average loss: " + averageLoss + ", runningWeight: " + runningWeightOfValidationSet);

        return averageLoss;


    }


}
