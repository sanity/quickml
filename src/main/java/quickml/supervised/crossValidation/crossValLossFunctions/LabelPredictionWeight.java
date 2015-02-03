package quickml.supervised.crossValidation.crossValLossFunctions;


import java.io.Serializable;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class LabelPredictionWeight<L, P> {
    double weight;
    L label;
    P prediction;

    public double getWeight() {
        return weight;
    }

    public L getLabel() {
        return label;
    }

    public P getPrediction() {
        return prediction;
    }

    public LabelPredictionWeight(L label, P prediction, double weight) {
        this.label = label;
        this.prediction = prediction;
        this.weight = weight;
    }
}
