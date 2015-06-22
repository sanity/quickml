package quickml.supervised.featureEngineering.enrichStrategies.attributeCombiner;

import com.google.common.base.Joiner;
import quickml.data.AttributesMap;
import quickml.supervised.featureEngineering.AttributesEnricher;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * See {@link quickml.supervised.featureEngineering.enrichStrategies.attributeCombiner.AttributeCombiningEnrichStrategy}
 */
public class AttributeCombiningEnricher implements AttributesEnricher {
    private static final long serialVersionUID = -7735359633536109195L;
    private final Set<List<String>> attributesToCombine;

    public AttributeCombiningEnricher(Set<List<String>> attributesToCombine) {
        this.attributesToCombine = attributesToCombine;
    }

    @Nullable
    @Override
    public AttributesMap apply(@Nullable final AttributesMap inputAttributes) {
        AttributesMap outputAttributes = AttributesMap.newHashMap();
        outputAttributes.putAll(inputAttributes);
        for (List<String> attributeKeys : attributesToCombine) {
            if (attributesNotCombinable(attributeKeys, inputAttributes))
                continue;
            StringBuilder values = new StringBuilder();
            for (String attributeKey : attributeKeys) {
                Object value = inputAttributes.get(attributeKey);
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

    private boolean attributesNotCombinable(List<String> attributeKeys, @Nullable final AttributesMap inputAttributes) {
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
