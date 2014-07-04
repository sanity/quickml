package quickdt.crossValidation;

import com.google.common.base.Preconditions;

/**
 * Created by alexanderhawk on 4/10/14.
 */
public class LogCrossValLossFunction extends OnlineCrossValLossFunction<LogCrossValLossFunction> {
    private static final double DEFAULT_MIN_PROBABILITY = 10E-16;
    public  double minProbability;
    public  double maxError;

    public LogCrossValLossFunction() {
        this(DEFAULT_MIN_PROBABILITY);
    }

    public LogCrossValLossFunction(double minProbability) {
        this.minProbability = minProbability;
        maxError = -Math.log(minProbability);
    }

    @Override
    public double getLossFromInstance(double probabilityOfCorrectInstance, double weight) {
        Preconditions.checkArgument(!Double.isNaN(probabilityOfCorrectInstance), "Probability must be a natural number, not NaN");
        Preconditions.checkArgument(!Double.isInfinite(probabilityOfCorrectInstance), "Probability must be a natural number, not infinite");

        final double error =  (probabilityOfCorrectInstance > minProbability) ? -weight*Math.log(probabilityOfCorrectInstance) : maxError;
        return error;

    }
    @Override
    public int compareTo(final LogCrossValLossFunction o) {
        return 1 - Double.compare( super.totalLoss, o.totalLoss);
    }


    @Override
    public String toString() {
        return "total LogLoss: "+ super.totalLoss;
    }
}
