package quickml.supervised.featureEngineering;

import com.google.common.base.Function;
import quickml.data.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 5/20/14.
 */
public class InstanceEnricher implements Function<Instance, Instance<Map<String,Serializable>>> {
    private final List<AttributesEnricher> attributesEnrichers;

    public InstanceEnricher(List<AttributesEnricher> attributesEnrichers) {
        this.attributesEnrichers = attributesEnrichers;
    }

    @Nullable
    @Override
    public Instance<Map<String, Serializable>> apply(@Nullable Instance instance) {
        Map<String, Serializable> enrichedAttributes = (Map<String, Serializable>) instance.getRegressors();
        for (AttributesEnricher attributesEnricher : attributesEnrichers) {
            enrichedAttributes = attributesEnricher.apply(enrichedAttributes);
        }
        return new InstanceImpl(enrichedAttributes, instance.getLabel(), instance.getWeight());
    }
}
