package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public abstract class TransformedData<I extends Instance, D extends Transformed<I>> extends Transformed<I> implements CopyableData<D, I> {
     public TransformedData() {
          super();
     }
}

