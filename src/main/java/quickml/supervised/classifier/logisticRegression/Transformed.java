package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public interface Transformed<R extends Instance> {
    public abstract List<R> getTransformedInstances();
}
