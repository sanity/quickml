package quickdt.predictiveModels.featureEngineering.enrichStrategies.attributeCombiner;

import com.google.common.base.Joiner;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.predictiveModels.featureEngineering.AttributesEnricher;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * See {@link quickdt.predictiveModels.featureEngineering.enrichStrategies.attributeCombiner.AttributeCombiningEnrichStrategy}
 */
public class AttributeCombiningEnricher implements AttributesEnricher {
    private static final long serialVersionUID = -7735359633536109195L;
    private final Set<List<String>> attributesToCombine;

    public AttributeCombiningEnricher(Set<List<String>> attributesToCombine) {
        this.attributesToCombine = attributesToCombine;
    }

    @Nullable
    @Override
    public Attributes apply(@Nullable final Attributes inputAttributes) {
        HashMapAttributes outputAttributes = new HashMapAttributes();
        outputAttributes.putAll(inputAttributes);
        for (List<String> attributeKeys : attributesToCombine) {
            if (attributesNotCombinable(attributeKeys, inputAttributes))
                continue;
            StringBuilder values = new StringBuilder();
            for (String attributeKey : attributeKeys) {
                Serializable value = inputAttributes.get(attributeKey);
                if (value != null && value.toString().length() > 0) {
                    values.append(value.toString());
                } else {
                    values.append("-"); 
                }
            }
            outputAttributes.put(Joiner.on('-').join(attributeKeys), values.toString());
        }
        return outputAttributes;
    }

    private boolean attributesNotCombinable(List<String> attributeKeys, @Nullable final Attributes inputAttributes) {
        for (String attribute : attributeKeys)
            if (!inputAttributes.containsKey(attribute))
                return true;
        return false;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        return false;
    }
}
