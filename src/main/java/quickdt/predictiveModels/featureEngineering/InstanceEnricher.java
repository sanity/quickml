package quickdt.predictiveModels.featureEngineering;

import com.google.common.base.Function;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;

import javax.annotation.Nullable;

/**
 * Created by ian on 5/20/14.
 */
public class InstanceEnricher implements Function<AbstractInstance, Instance> {
    private final AttributesEnricher attributesEnricher;

    public InstanceEnricher(AttributesEnricher attributesEnricher) {
        this.attributesEnricher = attributesEnricher;
    }

    @Nullable
    @Override
    public Instance apply(@Nullable final AbstractInstance instance) {
        return new Instance(attributesEnricher.apply(instance.getAttributes()), instance.getClassification(), instance.getWeight());
    }
}
