package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public interface TransformedData<R extends Instance, D extends TransformedData<R, D>> {
    D copyWithJustTrainingSet(List<R> trainingSet);
    List<R> getTransformedInstances();
}