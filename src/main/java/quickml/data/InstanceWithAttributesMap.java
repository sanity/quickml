package quickml.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class InstanceWithAttributesMap extends InstanceImpl<AttributesMap, Serializable> {
    public InstanceWithAttributesMap(final AttributesMap attributes, final Serializable label) {
        super(attributes, label, DEFAULT_WEIGHT);
    }

    public InstanceWithAttributesMap(final AttributesMap attributes, final Serializable label, final double weight) {
       super(attributes, label, weight);
    }

    public static InstanceWithAttributesMap create(final Serializable classification, final Serializable... inputs) {
        return create(classification, DEFAULT_WEIGHT, inputs);
    }

    public static InstanceWithAttributesMap create(final Serializable classification, final double weight, final Serializable... inputs) {
        final AttributesMap a = AttributesMap.newHashMap();
        for (int x = 0; x < inputs.length; x += 2) {
            a.put((String) inputs[x], inputs[x + 1]);
        }
        return new InstanceWithAttributesMap(a, classification, weight);
    }

}
