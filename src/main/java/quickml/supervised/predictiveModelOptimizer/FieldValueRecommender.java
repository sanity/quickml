package quickml.supervised.predictiveModelOptimizer;

import java.util.List;

/**
 * Created by ian on 4/12/14.
 */
public interface FieldValueRecommender {
    List<? extends Object> getValues();

    Object first();

    boolean shouldContinue(List<Double> losses);
}
