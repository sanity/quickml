package quickml.supervised.alternative.crossValidationLoss;

public interface ClassifierLossFunction {

    public double getLoss(PredictionMapResults results);

    public String getName();

}
