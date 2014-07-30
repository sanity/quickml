package quickdt.crossValidation.crossValLossFunctions;

import quickdt.predictiveModels.Prediction;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class LabelPredictionWeight {
    double weight;
    Serializable label;
    Prediction prediction;

    public LabelPredictionWeight(Serializable label, Prediction prediction, double weight) {
        this.label = label;
        this.prediction = prediction;
        this.weight = weight;
    }
}
