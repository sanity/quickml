package quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders;

import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;

import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

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
        checkArgument(values.size() > 0, "Must include at least one value");
        this.values = values;
        this.tolerance = tolerance;
        sortValues();
    }

    @Override
    public List<? extends Number> getValues() {
        return values;
    }

    @Override
    public Number first() {
        return values.get(0);
    }

    @Override
    public boolean shouldContinue(List<Double> losses) {
        int valuesTried = losses.size();
        double mostRecentLoss = losses.get(losses.size() - 1);
        double secondMostRecentLoss = losses.size() > 1 ? losses.get(losses.size() - 2) : 1;

        //if we have tried at least min values and we aren't within tolerance give up
        return !(withinTolerence(mostRecentLoss, secondMostRecentLoss) && valuesTried >= MIN_VALUES_TO_TEST);
    }

    private boolean withinTolerence(double mostRecentLoss, double secondMostRecentLoss) {
        return Math.abs(mostRecentLoss - secondMostRecentLoss) / secondMostRecentLoss > tolerance;
    }

    private void sortValues() {
        Collections.sort(values, new Comparator<Number>() {
            @Override
            public int compare(Number o1, Number o2) {
                return Double.compare(o1.doubleValue(), o2.doubleValue());
            }
        });
    }
}