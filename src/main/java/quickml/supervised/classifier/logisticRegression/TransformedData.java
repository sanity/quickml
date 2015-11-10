package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public interface TransformedData<R extends Instance, D extends Transformed<R>> extends Transformed<R>, CopyableData<R, D> {
     ///public TransformedData() {
       //   super();
    // }
}

