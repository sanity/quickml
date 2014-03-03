package quickdt.experiments.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

/**
 * Created by ian on 2/28/14.
 */
public class MSECrossValScorer extends CrossValScorer<MSECrossValScorer> {
    public static Supplier<MSECrossValScorer> supplier = new Supplier<MSECrossValScorer>() {
        @Override
        public MSECrossValScorer get() {
            return new MSECrossValScorer();
        }
    };

    private int total = 0;
    private double totalErrorSquared = 0.0;

    @Override
    public void score(double probabilityOfCorrectInstance, double weight) {
        Preconditions.checkArgument(!Double.isNaN(probabilityOfCorrectInstance), "Probability must be a natural number, not NaN");
        Preconditions.checkArgument(!Double.isInfinite(probabilityOfCorrectInstance), "Probability must be a natural number, not infinite");

        total+= weight;
        final double error = (1.0 - probabilityOfCorrectInstance) * weight;
        final double errorSquared = error*error;
        totalErrorSquared += errorSquared;
    }

    @Override
    public int compareTo(final MSECrossValScorer o) {
        return 1 - Double.compare(this.getMSE(), o.getMSE());
    }

    public double getMSE() {
        if (total == 0) {
            throw new IllegalStateException("Tried to get MSE but nothing has been reported to MSECrossValScorer");
        }
        return totalErrorSquared / total;
    }

    @Override
    public String toString() {
        return "MSE: "+getMSE();
    }
}
