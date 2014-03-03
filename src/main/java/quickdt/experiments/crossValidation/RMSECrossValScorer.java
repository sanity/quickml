package quickdt.experiments.crossValidation;

import com.google.common.base.Supplier;

/**
 * Created by ian on 2/28/14.
 */
public class RMSECrossValScorer extends CrossValScorer<RMSECrossValScorer> {
    public static Supplier<RMSECrossValScorer> supplier = new Supplier<RMSECrossValScorer>() {
         @Override
         public RMSECrossValScorer get() {
             return new RMSECrossValScorer();
         }
     };

    private MSECrossValScorer mseCrossValScorer = new MSECrossValScorer();

    @Override
    public void score(final double probabilityOfCorrectInstance, double weight) {
        mseCrossValScorer.score(probabilityOfCorrectInstance, weight);
    }

    @Override
    public int compareTo(final RMSECrossValScorer o) {
        return mseCrossValScorer.compareTo(o.mseCrossValScorer);
    }

    public double getRMSE() {
        return Math.sqrt(mseCrossValScorer.getMSE());
    }

    @Override
    public String toString() {
        return "RMSE: "+getRMSE();
    }
}
