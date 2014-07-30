package quickdt.crossValidation.crossValLossFunctions;

import quickdt.predictiveModels.Prediction;
import quickdt.Label;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class LabelPredictionWeight<T extends Serializable> {
    double weight;
    Label<T> label;
    Prediction<T> prediction;

    public LabelPredictionWeight(Label<T> label, Prediction<T> prediction, double weight) {
        this.label = label;
        this.prediction = prediction;
        this.weight = weight;
    }
}
