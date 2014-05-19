package quickdt.crossValidation;

import com.google.common.base.Preconditions;

/**
 * Created by alexanderhawk on 4/10/14.
 */
public class LogCrossValLoss extends OnlineCrossValLoss<LogCrossValLoss> {

    public static final double DEFAULT_MIN_PROBABILITY = 10e-8;
    double totalLogLoss = 0;
    private double total = 0;
    public final double minProbability;
    public final double maxError;

    public LogCrossValLoss() {
        this(DEFAULT_MIN_PROBABILITY);
    }

    public LogCrossValLoss(double minProbability) {
        this.minProbability = minProbability;
        maxError = -Math.log(minProbability);
    }

    @Override
    public void addLossFromInstance(double probabilityOfCorrectInstance, double weight) {
        Preconditions.checkArgument(!Double.isNaN(probabilityOfCorrectInstance), "Probability must be a natural number, not NaN");
        Preconditions.checkArgument(!Double.isInfinite(probabilityOfCorrectInstance), "Probability must be a natural number, not infinite");

        total+= weight;
        final double error =  (probabilityOfCorrectInstance > minProbability) ? -weight*Math.log(probabilityOfCorrectInstance) : maxError;
        totalLogLoss += error;

    }

    @Override
    public int compareTo(final LogCrossValLoss o) {
        return 1 - Double.compare(this.getTotalLoss(), o.getTotalLoss());
    }

    @Override
    public double getTotalLoss() {
        if (total == 0) {
            throw new IllegalStateException("Tried to get LogLoss but nothing has been reported to LogCrossValLoss");
        }
        return totalLogLoss / total;
    }

    @Override
    public String toString() {
        return "total LogLoss: "+ getTotalLoss();
    }
}
