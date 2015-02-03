package quickml.supervised.featureEngineering;

import com.google.common.base.Function;
import quickml.data.AttributesMap;
import quickml.supervised.alternative.optimizer.ClassifierInstance;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by ian on 5/20/14.
 */
public class InstanceEnricher implements Function<ClassifierInstance, ClassifierInstance> {
    private final List<AttributesEnricher> attributesEnrichers;

    public InstanceEnricher(List<AttributesEnricher> attributesEnrichers) {
        this.attributesEnrichers = attributesEnrichers;
    }

    @Nullable
    @Override
    public ClassifierInstance apply(@Nullable ClassifierInstance instance) {
        AttributesMap attributes = instance.getAttributes();
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            attributes = attributesEnricher.apply(attributes);
        }
        return new ClassifierInstance(attributes, instance.getLabel(), instance.getWeight());
    }
}
