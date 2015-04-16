package quickml.data;

/**
 * Created by alexanderhawk on 4/14/15.
 */
public class ClassifierInstance extends InstanceWithAttributesMap<Object> {
    public ClassifierInstance(AttributesMap attributes, Object label) {
        super(attributes, label, 1.0);
    }
}

