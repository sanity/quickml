package quickml.supervised.crossValidation;

import quickml.data.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 10/10/14.
 */
public interface LabelConverter<R, L> {
    List<Instance<R, L>> convertLabels(List<Instance<R, L>> initialInstances);
}
