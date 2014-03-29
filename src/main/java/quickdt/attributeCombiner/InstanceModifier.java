package quickdt.attributeCombiner;

import com.google.common.base.Function;
import quickdt.*;

/**
 * Created by ian on 3/29/14.
 */
public class InstanceModifier implements Function<AbstractInstance, Instance> {
    private final Function<Attributes, Attributes> attributeModifier;

    public InstanceModifier(Function<Attributes, Attributes> attributeModifier) {
        this.attributeModifier = attributeModifier;
    }

    @Override
    public Instance apply(final AbstractInstance abstractInstance) {
        return new Instance(attributeModifier.apply(abstractInstance.getAttributes()), abstractInstance.getClassification());
    }
}
