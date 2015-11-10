package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public interface CopyableData<R extends Instance, T extends Transformed<R>> {
    T copyWithJustTrainingSet(List<R> trainingSet);
}


