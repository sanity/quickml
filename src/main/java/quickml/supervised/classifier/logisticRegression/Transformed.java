package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public abstract class Transformed<I extends Instance> {
    public abstract List<I> getTransformedInstances();
}
