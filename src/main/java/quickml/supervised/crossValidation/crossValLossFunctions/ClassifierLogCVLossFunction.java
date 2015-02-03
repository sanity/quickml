package quickml.supervised.crossValidation.crossValLossFunctions;

import com.google.common.base.Preconditions;

/**
 * Created by alexanderhawk on 4/10/14.
 */
public class ClassifierLogCVLossFunction extends OnlineClassifierCVLossFunction {
    private static final double DEFAULT_MIN_PROBABILITY = 10E-7;
    public double minProbability;
    public double maxError;

    public ClassifierLogCVLossFunction() {
        this(DEFAULT_MIN_PROBABILITY);
    }

    public ClassifierLogCVLossFunction(double minProbability) {
        this.minProbability = minProbability;
        maxError = -Math.log(minProbability);
    }

    @Override
    public double getLossFromInstance(double probabilityOfCorrectInstance, double weight) {
        Preconditions.checkArgument(!Double.isNaN(probabilityOfCorrectInstance), "Probability must be a natural number, not NaN");
        Preconditions.checkArgument(!Double.isInfinite(probabilityOfCorrectInstance), "Probability must be a natural number, not infinite");

        return (probabilityOfCorrectInstance > minProbability) ? -weight * Math.log(probabilityOfCorrectInstance) : weight * maxError;
    }

    @Override
    public String toString() {
        return "total LogLoss: " + super.totalLoss;
    }
}
