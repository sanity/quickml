package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public interface CopyableData<T extends Transformed, I extends Instance> {
    T copyWithJustTraniningSet(List<I> trainingSet);
}


