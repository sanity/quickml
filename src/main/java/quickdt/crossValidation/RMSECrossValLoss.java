package quickdt.crossValidation;

import com.google.common.base.Supplier;

/**
 * Created by ian on 2/28/14.
 */
public class RMSECrossValLoss extends OnlineCrossValLoss<RMSECrossValLoss> {
    public static Supplier<RMSECrossValLoss> supplier = new Supplier<RMSECrossValLoss>() {
         @Override
         public RMSECrossValLoss get() {
             return new RMSECrossValLoss();
         }
     };

    private MSECrossValLoss mseCrossValLoss = new MSECrossValLoss();

    @Override
    public void addLossFromInstance(final double probabilityOfCorrectInstance, double weight) {
        mseCrossValLoss.addLossFromInstance(probabilityOfCorrectInstance, weight);
    }

    @Override
    public int compareTo(final RMSECrossValLoss o) {
        return mseCrossValLoss.compareTo(o.mseCrossValLoss);
    }
    @Override
    public double getTotalLoss() {
        return Math.sqrt(mseCrossValLoss.getTotalLoss());
    }

    @Override
    public String toString() {
        return "RMSE: "+ getTotalLoss();
    }
}
