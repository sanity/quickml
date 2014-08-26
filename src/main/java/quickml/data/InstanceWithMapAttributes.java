package quickml.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class InstanceWithMapAttributes extends InstanceImpl<Map<String, Serializable>> {
    public InstanceWithMapAttributes(final Map<String, Serializable> attributes, final Serializable label) {
        super(attributes, label, DEFAULT_WEIGHT);
    }

    public InstanceWithMapAttributes(final Map<String, Serializable> attributes, final Serializable label, final double weight) {
       super(attributes, label, weight);
    }

}
