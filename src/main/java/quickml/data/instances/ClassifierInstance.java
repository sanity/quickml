package quickml.data.instances;

import quickml.data.AttributesMap;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 4/14/15.
 */
public class ClassifierInstance extends InstanceWithAttributesMap<Serializable> {
    public ClassifierInstance(AttributesMap attributes, Serializable label) {
        super(attributes, label, 1.0);
    }
    public ClassifierInstance(AttributesMap attributes, Serializable label, double weight) {
        super(attributes, label, weight);
    }

}

