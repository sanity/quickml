package quickml.supervised.alternative.crossvalidation;

public abstract class ClassifierLossFunction implements LossFunction<Double, PredictionMapResults> {

    public abstract Double getLoss(PredictionMapResults results);

    public abstract String getName();

}
