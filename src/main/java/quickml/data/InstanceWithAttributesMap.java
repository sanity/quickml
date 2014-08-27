package quickml.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class InstanceWithAttributesMap extends InstanceImpl<AttributesMap> {
    public InstanceWithAttributesMap(final AttributesMap attributes, final Serializable label) {
        super(attributes, label, DEFAULT_WEIGHT);
    }

    public InstanceWithAttributesMap(final AttributesMap attributes, final Serializable label, final double weight) {
       super(attributes, label, weight);
    }

}
