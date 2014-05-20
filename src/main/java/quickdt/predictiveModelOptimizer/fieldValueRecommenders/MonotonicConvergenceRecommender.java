package quickdt.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.base.Optional;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;

import java.util.*;

/**
 * Created by ian on 4/12/14.
 */
public class MonotonicConvergenceRecommender implements FieldValueRecommender {
    private static final int MIN_VALUES_TO_TEST = 2;
    private static final double DEFAULT_TOLERANCE = 0.02;

    private final List<? extends Number> values;
    private final double tolerance;

    public MonotonicConvergenceRecommender(List<? extends Number> values) {
        this(values, DEFAULT_TOLERANCE);
    }

    public MonotonicConvergenceRecommender(List<? extends Number> values, double tolerance) {
        this.values = values;
        this.tolerance = tolerance;
        sortValues();
    }

    @Override
    public Optional<Object> recommendNextValue(final Map<Object, Double> valuesPreviouslyAttemptedWithLoss) {
        int valuesTried = 0;
        double mostRecentLoss = 1;
        double secondMostRecentLoss = 1;
        for (Number value : values) {
            if (valuesPreviouslyAttemptedWithLoss.containsKey(value)) {
                secondMostRecentLoss = mostRecentLoss;
                mostRecentLoss = valuesPreviouslyAttemptedWithLoss.get(value);
            } else {
                break;
            }
            valuesTried++;
        }

        //if we have tried at least min values and we aren't within tolerance give up
        if (mostRecentLossNotWithinTolerance(mostRecentLoss, secondMostRecentLoss) && valuesTried >= MIN_VALUES_TO_TEST) {
            return Optional.absent();
        }

        //get next value that hasn't been attempted
        for (Number value : values) {
            if (!valuesPreviouslyAttemptedWithLoss.containsKey(value)) {
                return Optional.of((Object) value);
            }
        }

        return Optional.absent();  //we hit this when all values have been tried.
    }

    private void sortValues() {
        Collections.sort(values, new Comparator<Number>() {
            @Override
            public int compare(Number o1, Number o2) {
                double d1 = o1.doubleValue();
                double d2 = o2.doubleValue();
                if (d1 > d2) {
                    return 1;
                } else if (d1 == d2) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });
    }

    private boolean mostRecentLossNotWithinTolerance(double mostRecentLoss, double secondMostRecentLoss) {
        return Math.abs(mostRecentLoss - secondMostRecentLoss) / secondMostRecentLoss > tolerance;
    }

}
