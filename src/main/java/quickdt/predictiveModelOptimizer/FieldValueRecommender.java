package quickdt.predictiveModelOptimizer;

import com.google.common.base.Optional;

import java.util.Map;

/**
 * Created by ian on 4/12/14.
 */
public interface FieldValueRecommender {
    public Optional<Object> recommendNextValue(Map<Object, Double> valuesPreviouslyAttemptedWithLoss);
}
