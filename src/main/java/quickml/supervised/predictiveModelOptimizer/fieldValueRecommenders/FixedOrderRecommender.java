package quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.collect.Lists;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class FixedOrderRecommender implements FieldValueRecommender {
    private final List<Serializable> values;

    public FixedOrderRecommender(Serializable... values) {
        checkArgument(values.length > 0, "Must include at least one value");
        this.values = Lists.newArrayList(values);
    }

    @Override
    public List<Serializable> getValues() {
        return values;
    }

    @Override
    public Serializable first() {
        return values.get(0);
    }

    @Override
    public boolean shouldContinue(List<Double> losses) {
        return false;
    }



}


