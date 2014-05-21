package quickdt.predictiveModels.featureEngineering;

import com.google.common.base.Function;
import quickdt.data.*;

import java.util.List;

/**
 * Created by ian on 5/20/14.
 */
public class InstanceEnricher implements Function<AbstractInstance, Instance> {
    private final List<AttributesEnricher> attributesEnrichers;

    public InstanceEnricher(List<AttributesEnricher> attributesEnrichers) {
        this.attributesEnrichers = attributesEnrichers;
    }

    @Override
    public Instance apply(final AbstractInstance instance) {
        Attributes enrichedAttributes = instance.getAttributes();
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            enrichedAttributes = attributesEnricher.apply(enrichedAttributes);
        }
        return new Instance(enrichedAttributes, instance.getClassification(), instance.getWeight());
    }
}
