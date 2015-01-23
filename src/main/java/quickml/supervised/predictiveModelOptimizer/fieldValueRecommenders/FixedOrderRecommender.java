package quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 4/12/14.
 */
public class FixedOrderRecommender implements FieldValueRecommender {
    private final List<Object> values;
    private int index = 0;

    public FixedOrderRecommender(Object... values) {
        if (values.length <= 0)
            throw new RuntimeException("Must include at least one value");
        this.values = Lists.newArrayList(values);
    }

    public List<Object> getValues() {
        return values;
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

    public Object first() {
        index++;
        return values.get(0);
    }

    public Optional<Object> next() {
        if (index >= values.size())
            return Optional.absent();
        else {
            index++;
            return Optional.of(values.get(index));
        }
    }


}
