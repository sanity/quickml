package quickdt.crossValidation;

import com.google.common.base.Preconditions;
<<<<<<< HEAD
import com.google.common.base.Supplier;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import sun.rmi.runtime.Log;
=======
>>>>>>> master

import java.util.List;

/**
 * Created by alexanderhawk on 4/10/14.
 */
public class LogCrossValLoss extends OnlineCrossValLoss<LogCrossValLoss> {

    public  double minProbability;
    public  double maxError;

    public LogCrossValLoss() {
        this(DEFAULT_MIN_PROBABILITY);
    }

    public LogCrossValLoss(double minProbability) {
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
    public int compareTo(final LogCrossValLoss o) {
        return 1 - Double.compare( super.totalLoss, o.totalLoss);
    }


    @Override
    public String toString() {
        return "total LogLoss: "+ super.totalLoss;
    }
}
