package quickml.supervised.predictiveModelOptimizer;

import java.util.List;

/**
 * Created by ian on 4/12/14.
 */
public interface FieldValueRecommender {
    List<Object> getValues();

    Object first();
}
