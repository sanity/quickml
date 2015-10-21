package quickml.data.instances;

import quickml.data.AttributesMap;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class ClassifierInstanceFactory implements InstanceFactory<ClassifierInstance, AttributesMap, Serializable> {
    @Override
    public ClassifierInstance createInstance(AttributesMap attributes, Serializable label, double weight) {
        return new ClassifierInstance(attributes, label, weight);
    }
}
