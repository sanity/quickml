package quickml.supervised.dataProcessing.instanceTranformer;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import quickml.data.AttributesMap;
import quickml.data.instances.InstanceFactory;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.dataProcessing.AttributeCharacteristics;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils.oneHotEncode;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class OneHotEncoder<L extends Serializable, I extends InstanceWithAttributesMap<L>, R extends InstanceWithAttributesMap<Serializable>> implements InstanceTransformer<I, R> {
    public static final String INSUFFICIENT_CAT_ATTR = "insufficientCatAttr";
    final Map<String, AttributeCharacteristics> attributeCharacteristics;
    final InstanceFactory<R, AttributesMap, L> instanceFactory;
    final int minObservationsOfAnAtribute;

    public OneHotEncoder(Map<String, AttributeCharacteristics> attributeCharacteristics, final InstanceFactory<R, AttributesMap, L> instanceFactory, int minObservationsOfAnAtribute) {
        this.attributeCharacteristics = attributeCharacteristics;
        this.instanceFactory = instanceFactory;
        this.minObservationsOfAnAtribute = minObservationsOfAnAtribute;
    }

    public OneHotEncoder(Map<String, AttributeCharacteristics> attributeCharacteristics, final InstanceFactory<R, AttributesMap, L> instanceFactory) {
        this(attributeCharacteristics, instanceFactory, 1);
    }

    public List<R> transformAll(List<I> instances) {
        Object2LongArrayMap<String> expandedCatAttributeToCounts = new Object2LongArrayMap<>();
        Object2LongArrayMap<String> attributeToCounts = new Object2LongArrayMap<>();

        for (I instance : instances) {
            for (Map.Entry<String, Serializable> entry : instance.getAttributes().entrySet()) {
                String attribute = entry.getKey();
                updateCounts(attributeToCounts, attribute);
                if (!attributeCharacteristics.get(attribute).isNumber) {
                    String expandedAttribute = oneHotEncode(attribute, entry.getValue());
                    updateCounts(expandedCatAttributeToCounts, expandedAttribute);
                }
            }
        }
        List<R> transformed = Lists.newArrayList();
        for (I instance : instances) {
            transformed.add(transformInstance(instance, expandedCatAttributeToCounts, attributeToCounts, minObservationsOfAnAtribute));
        }
        return transformed;
    }

    private void updateCounts(Object2LongArrayMap<String> counts, String key) {
        if (!counts.containsKey(key)) {
            counts.put(key, 0L);
        }
        counts.put(key, counts.getLong(key) + 1L);
    }

    public R transformInstance(I instance, Object2LongArrayMap<String> expandedCatAttributeToCounts, Object2LongArrayMap<String> attributeToCounts, int minObservationsOfAnAtribute) {
        /**attributes with insufficient data are ignored altogether...less arbitrary than counting number of attributes with insufficient-data
         **/
        AttributesMap attributesMap = AttributesMap.newHashMap();
        AttributesMap rawAttributes = instance.getAttributes();
        for (Map.Entry<String, Serializable> entry : rawAttributes.entrySet()) {
            String attribute = entry.getKey();
            if (!attributeCharacteristics.get(attribute).isNumber) {
                String expandedAttribute = oneHotEncode(attribute, entry.getValue());
                if (expandedCatAttributeToCounts.get(expandedAttribute) >= (long) minObservationsOfAnAtribute) {
                    attributesMap.put(expandedAttribute, 1.0);
                } else if (attributeToCounts.get(attribute) >= (long) minObservationsOfAnAtribute) {
                    String insufficientDataAttribute = attribute + "--" + INSUFFICIENT_CAT_ATTR;
                    attributesMap.put(insufficientDataAttribute, 1.0);
                }
            } else {
                if (attributeToCounts.get(attribute) >= (long) minObservationsOfAnAtribute) {
                    attributesMap.put(attribute, ((Number) entry.getValue()).doubleValue());
                }
            }
        }
        return instanceFactory.createInstance(attributesMap, instance.getLabel(), instance.getWeight());
    }

    @Override
    public R transformInstance(I instance) {
        AttributesMap attributesMap = AttributesMap.newHashMap();
        AttributesMap rawAttributes = instance.getAttributes();
        for (String key : rawAttributes.keySet()) {
            if (!attributeCharacteristics.get(key).isNumber) {
                attributesMap.put(oneHotEncode(key, rawAttributes.get(key)), 1.0);
            } else {
                attributesMap.put(key, ((Number) rawAttributes.get(key)).doubleValue());
            }
        }
        return instanceFactory.createInstance(attributesMap, instance.getLabel(), instance.getWeight());
    }
}
