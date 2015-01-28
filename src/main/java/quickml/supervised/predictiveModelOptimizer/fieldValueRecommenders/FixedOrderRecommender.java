package quickml.supervised.predictiveModelOptimizer.fieldValueRecommenders;

import com.google.common.collect.Lists;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;

import java.util.List;

//TODO[mk] maybe just replace with a list or array?
public class FixedOrderRecommender implements FieldValueRecommender {
    private final List<Object> values;

    public FixedOrderRecommender(Object... values) {
        if (values.length <= 0)
            throw new RuntimeException("Must include at least one value");
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

}
