package quickdt.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import quickdt.predictiveModelOptimizer.FieldValueRecommender;

import java.util.Map;

/**
 * Created by ian on 4/12/14.
 */
public class FixedOrderRecommender implements FieldValueRecommender {
    private final Iterable<Object> values;

    public FixedOrderRecommender(Object ... values) {
        this(Lists.newArrayList(values));
    }

    public FixedOrderRecommender(Iterable<Object> values) {
        this.values = values;
    }

    @Override
    public Optional<Object> recommendNextValue(final Map<Object, Double> valuesPreviouslyAttemptedWithLoss) {
        for (Object value : values) {
            if (!valuesPreviouslyAttemptedWithLoss.containsKey(value)) {
                return Optional.of(value);
            }
        }
        return Optional.absent();
    }
}
