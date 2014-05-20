package quickdt.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.decisionTree.Tree;

import java.util.*;

/**
 * Created by ian on 4/12/14.
 */
public class MonotonicConvergenceRecommender implements FieldValueRecommender {
    private final List<? extends Number> values;
    private double lowestLossSoFar;
    private double tolerance;
    private static final double DEFAULT_TOLERANCE = 0.02;
    double mostRecentLoss, secondMostRecentLoss;


    public MonotonicConvergenceRecommender(List<? extends Number> values, double tolerance) {
        this.values = values;
        this.tolerance = tolerance;
    }

    @Override
    public Optional<Object> recommendNextValue(final Map<Object, Double> valuesPreviouslyAttemptedWithLoss) {
        Collections.sort(values, new Comparator<Number>() {
            @Override
            public int compare(Number o1, Number o2) {
                double d1 = o1.doubleValue();
                double d2 = o2.doubleValue();
                if (d1 > d2)
                    return 1;
                else if (d1 == d2)
                    return 0;
                else
                    return -1;
            }
        });
        int valuesTried = 0;
        mostRecentLoss = 1;
        secondMostRecentLoss = 1;
        for (Number value : values) {  //its a precondition that the keys of valuesPreviouslyAttemptedWithLoss are Numbers
            if (valuesPreviouslyAttemptedWithLoss.containsKey(value)) {
                secondMostRecentLoss = mostRecentLoss;
                mostRecentLoss = valuesPreviouslyAttemptedWithLoss.get(value);
            } else
                break;
            valuesTried++;
        }

        if (mostRecentLossNotWithinTolerance() && valuesTried >=2)
            return Optional.absent();


        for (Number value : values)
            if (!valuesPreviouslyAttemptedWithLoss.containsKey(value)) {
                return Optional.of((Object)value);
            }

        return Optional.absent();  //we hit this when all values have been tried.
    }

    private boolean mostRecentLossNotWithinTolerance() {
        return Math.abs(mostRecentLoss - secondMostRecentLoss) / secondMostRecentLoss > tolerance;
    }

}
