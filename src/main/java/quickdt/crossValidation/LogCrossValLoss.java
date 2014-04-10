package quickdt.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import sun.rmi.runtime.Log;

/**
 * Created by alexanderhawk on 4/10/14.
 */
public class LogCrossValLoss extends CrossValLoss<LogCrossValLoss> {

    double totalLogLoss = 0;
    private double total = 0;
    public  double minProbability;
    public  double maxError;

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
