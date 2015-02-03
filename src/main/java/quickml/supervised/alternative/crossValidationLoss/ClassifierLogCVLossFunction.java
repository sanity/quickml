package quickml.supervised.alternative.crossValidationLoss;

public class ClassifierLogCVLossFunction implements ClassifierLossFunction {

    private static final double DEFAULT_MIN_PROBABILITY = 10E-7;
    public double minProbability;
    public double maxError;


    public ClassifierLogCVLossFunction(double minProbability) {
        this.minProbability = minProbability;
        this.maxError = -Math.log(minProbability);
    }

    private double doThis(double correctProbability, double weight) {
        return (correctProbability > minProbability) ? -weight * Math.log(correctProbability) : weight * maxError;
    }

    @Override
    public double getLoss(PredictionMapResults results) {
        double totalLoss = 0;
        double weight = results.totalWeight();
        for (PredictionMapResult predictionMapResult : results) {
            totalLoss += doThis(predictionMapResult.getPredictionForLabel(), weight);
        }
        return weight > 0 ? totalLoss / weight : 0;
    }

    @Override
    public String getName() {
        return "LOG_CV";
    }
}
