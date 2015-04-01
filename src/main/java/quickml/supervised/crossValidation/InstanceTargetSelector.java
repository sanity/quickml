package quickml.supervised.crossValidation;

import quickml.data.ClassifierInstance;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 4/1/15.
 */
public interface InstanceTargetSelector<T extends ClassifierInstance> {
    Serializable getSingleLabel(T instance);
}
