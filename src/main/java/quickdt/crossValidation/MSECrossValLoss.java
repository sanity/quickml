package quickdt.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

/**
 * Created by ian on 2/28/14.
 */
public class MSECrossValLoss extends CrossValLoss<MSECrossValLoss> {
    public static Supplier<MSECrossValLoss> supplier = new Supplier<MSECrossValLoss>() {
        @Override
        public MSECrossValLoss get() {
            return new MSECrossValLoss();
        }
    };

    private int total = 0;
    private double totalErrorSquared = 0.0;

    @Override
    public void addLossFromInstance(double probabilityOfCorrectInstance, double weight) {
        Preconditions.checkArgument(!Double.isNaN(probabilityOfCorrectInstance), "Probability must be a natural number, not NaN");
        Preconditions.checkArgument(!Double.isInfinite(probabilityOfCorrectInstance), "Probability must be a natural number, not infinite");

        total+= weight;
        final double error = (1.0 - probabilityOfCorrectInstance);
        final double errorSquared = error*error*weight;
        totalErrorSquared += errorSquared;
    }

    @Override
    public int compareTo(final MSECrossValLoss o) {
        return 1 - Double.compare(this.getTotalLoss(), o.getTotalLoss());
    }

    @Override
    public double getTotalLoss() {
        if (total == 0) {
            throw new IllegalStateException("Tried to get MSE but nothing has been reported to MSECrossValLoss");
        }
        return totalErrorSquared / total;
    }

    @Override
    public String toString() {
        return "MSE: "+ getTotalLoss();
    }
}
