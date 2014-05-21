package quickdt.predictiveModels.featureEngineering;

import com.google.common.base.Function;
import quickdt.data.*;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by ian on 5/20/14.
 */
public class InstanceEnricher implements Function<AbstractInstance, Instance> {
    private final List<AttributesEnricher> attributesEnrichers;

    public InstanceEnricher(List<AttributesEnricher> attributesEnrichers) {
        this.attributesEnrichers = attributesEnrichers;
    }

    @Nullable
    @Override
    public Instance apply(@Nullable final AbstractInstance instance) {
        Attributes enrichedAttributes = instance.getAttributes();
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            enrichedAttributes = attributesEnricher.apply(enrichedAttributes);
        }
        return new Instance(enrichedAttributes, instance.getClassification(), instance.getWeight());
    }
}
