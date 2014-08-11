package quickdt.predictiveModels.featureEngineering;

import com.google.common.base.Function;
import quickdt.data.*;

import java.util.List;

/**
 * Created by ian on 5/20/14.
 */
public class InstanceEnricher implements Function<Instance, InstanceWithMapOfRegressors> {
    private final List<AttributesEnricher> attributesEnrichers;

    public InstanceEnricher(List<AttributesEnricher> attributesEnrichers) {
        this.attributesEnrichers = attributesEnrichers;
    }

    @Override
    public InstanceWithMapOfRegressors apply(final Instance instance) {
        Map<String, Serializable> enrichedAttributes = instance.getRegressors();
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            enrichedAttributes = attributesEnricher.apply(enrichedAttributes);
        }
        return new InstanceWithMapOfRegressors(enrichedAttributes, instance.getLabel(), instance.getWeight());
    }
}
