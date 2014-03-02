package quickdt.experiments.crossValidation;

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
    public void score(double probabilityOfCorrectInstance) {
        total++;
        final double error = 1.0 - probabilityOfCorrectInstance;
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
