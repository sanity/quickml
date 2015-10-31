package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 10/28/15.
 */
public interface DataTransformer<I extends Instance, R extends Instance, D extends TransformedData<R, D>> {
    D transformData(List<I> instances);
}
