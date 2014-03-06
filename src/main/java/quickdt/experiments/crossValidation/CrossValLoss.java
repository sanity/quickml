package quickdt.experiments.crossValidation;

/**
 * Created by ian on 2/28/14.
 */
public abstract class CrossValLoss<S extends CrossValLoss> implements Comparable<S> {
    public abstract void addLossFromInstance(double probabilityOfCorrectInstance, double weight);
    public abstract double getTotalLoss();
}
