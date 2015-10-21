package quickml.supervised.featureEngineering1;

import com.google.common.base.Function;
import quickml.data.AttributesMap;
import quickml.data.instances.InstanceWithAttributesMap;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by ian on 5/20/14.
 */
public class InstanceEnricher implements Function<InstanceWithAttributesMap<?>, InstanceWithAttributesMap<?>> {
    private final List<AttributesEnricher> attributesEnrichers;

    public InstanceEnricher(List<AttributesEnricher> attributesEnrichers) {
        this.attributesEnrichers = attributesEnrichers;
    }

    @Nullable
    @Override
    public InstanceWithAttributesMap<?> apply(@Nullable InstanceWithAttributesMap instance) {
        AttributesMap attributes = instance.getAttributes();
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            attributes = attributesEnricher.apply(attributes);
        }
        return new InstanceWithAttributesMap(attributes, instance.getLabel(), instance.getWeight());
    }
}
