package quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.collect.Lists;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class FixedOrderRecommender implements FieldValueRecommender {
    private final List<Object> values;

    public FixedOrderRecommender(Object... values) {
        checkArgument(values.length > 0, "Must include at least one value");
        this.values = Lists.newArrayList(values);
    }

    @Override
    public List<Object> getValues() {
        return values;
    }

    @Override
    public Object first() {
        return values.get(0);
    }

    @Override
    public boolean shouldContinue(List<Double> losses) {
        return false;
    }



}


