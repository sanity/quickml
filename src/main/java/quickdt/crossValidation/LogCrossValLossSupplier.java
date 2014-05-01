package quickdt.crossValidation;

import com.google.common.base.Supplier;

/**
 * Created by alexanderhawk on 4/10/14.
 */
public class LogCrossValLossSupplier implements Supplier<LogCrossValLoss> {
    double minProbality;
    public LogCrossValLossSupplier() {
      this(LogCrossValLoss.DEFAULT_MIN_PROBABILITY);
    }
    public LogCrossValLossSupplier(double minProbability) {
        this.minProbality= minProbability;
    }
    @Override
    public LogCrossValLoss get() {
        return new LogCrossValLoss(minProbality);
    }

}

