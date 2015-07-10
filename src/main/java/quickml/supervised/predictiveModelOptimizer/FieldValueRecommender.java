package quickml.supervised.predictiveModelOptimizer;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ian on 4/12/14.
 */
public interface FieldValueRecommender {
    List<? extends Serializable> getValues();

    Serializable first();

    boolean shouldContinue(List<Double> losses);
}
