package quickdt.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

/**
 * Created by ian on 2/28/14.
 */
public class MSECrossValLoss extends OnlineCrossValLoss<MSECrossValLoss> {

    @Override
    public double getLossFromInstance(double probabilityOfCorrectInstance, double weight) {
        Preconditions.checkArgument(!Double.isNaN(probabilityOfCorrectInstance), "Probability must be a natural number, not NaN");
        Preconditions.checkArgument(!Double.isInfinite(probabilityOfCorrectInstance), "Probability must be a natural number, not infinite");

        final double error = (1.0 - probabilityOfCorrectInstance);
        final double errorSquared = error*error*weight;
        return errorSquared;
    }

    @Override
    public int compareTo(final MSECrossValLoss o) {
        return 1 - Double.compare(super.totalLoss, o.totalLoss);
    }

    @Override
    public String toString() {
        return "MSE: "+ super.totalLoss;
    }
}
