package quickdt.attributeCombiner;

import com.google.common.base.Function;
import quickdt.*;

import java.util.Collection;

/**
 * Created by ian on 3/29/14.
 */
public class InstanceAttributeFilter implements Function<AbstractInstance, Instance> {

    private final Collection<String> attributesToPermit;

    public InstanceAttributeFilter(Collection<String> attributesToPermit) {
        this.attributesToPermit = attributesToPermit;
    }

    @Override
    public Instance apply(final AbstractInstance instance) {
        HashMapAttributes newAttributes = new HashMapAttributes();
        for (String permittedAttribute : attributesToPermit) {
            newAttributes.put(permittedAttribute, instance.getAttributes().get(permittedAttribute));
        }
        return new Instance(newAttributes, instance.getClassification());
    }
}
