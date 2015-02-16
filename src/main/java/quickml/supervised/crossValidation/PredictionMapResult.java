package quickml.supervised.crossValidation;

import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;

public class PredictionMapResult extends LabelPredictionWeight<Serializable, PredictionMap> {
    private PredictionMap prediction;
    private Serializable label;
    private double weight;

    public PredictionMapResult(PredictionMap prediction, Serializable label, double weight) {
        super(label, prediction, weight);
        this.prediction = prediction;
        this.label = label;
        this.weight = weight;
    }

    public PredictionMap getPrediction() {
        return prediction;
    }

    public double getWeight() {
        return weight;
    }

    public Serializable getLabel() {
        return label;
    }

    public double getPredictionForLabel() {
        Double probability = prediction.get(label);
        checkArgument(!isNaN(probability), "Probability must be a natural number, not NaN");
        checkArgument(!isInfinite(probability), "Probability must be a natural number, not infinite");

        return probability;
    }
}
