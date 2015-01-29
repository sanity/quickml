package quickml.supervised.alternative.crossValidationLoss;

import quickml.data.PredictionMap;

import java.io.Serializable;
import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;

public class PredictionMapResults {

    private final ArrayList<PredictionMapResult> results;

    public PredictionMapResults() {
        results = new ArrayList<>();
    }

    public void addResult(PredictionMap predictionMap, Serializable label, double weight) {
        results.add(new PredictionMapResult(predictionMap, label, weight));
    }

    public double mseLoss() {
        int totalLoss = 0;
        for (PredictionMapResult result : results) {
            final double error = (1.0 - result.getPredictionForLabel());
            final double errorSquared = error * error * result.weight;
            totalLoss += errorSquared;
        }
        return totalWeight() > 0 ? totalLoss / totalWeight() : 0;
    }

    public double rmseLoss() {
        return Math.sqrt(mseLoss());
    }



    public double totalWeight() {
        double totalWeight = 0;
        for (PredictionMapResult result : results) {
            totalWeight += result.weight;
        }
        return totalWeight;
    }

    public ArrayList<PredictionMapResult> getResults() {
        return results;
    }

    public class PredictionMapResult {
        private PredictionMap prediction;
        private Serializable label;
        private double weight;

        public PredictionMapResult(PredictionMap prediction, Serializable label, double weight) {
            this.prediction = prediction;
            this.label = label;
            this.weight = weight;
        }

        public double getPredictionForLabel() {
            Double probability = prediction.get(label);
            checkArgument(!isNaN(probability), "Probability must be a natural number, not NaN");
            checkArgument(!isInfinite(probability), "Probability must be a natural number, not infinite");

            return probability;
        }

        public double getWeight() {
            return weight;
        }
    }


}
