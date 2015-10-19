package quickml.supervised.dataProcessing.instanceTranformer;

import quickml.data.AttributesMap;

import quickml.data.instances.InstanceFactory;
import quickml.data.instances.InstanceWithAttributesMap;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Created by chrisreeves on 10/14/15.
 */
public class ProductFeatureAppender<L extends Serializable, I extends InstanceWithAttributesMap<L>, R extends InstanceWithAttributesMap<L>> implements InstanceTransformer<I, R> {
    private final Map<String, Integer> attributeProductCounts;
    private final InstanceFactory<R, AttributesMap, L> instanceFactory;
    private final Integer minimumCount;

    public ProductFeatureAppender(Map<String, Integer> attributeProductCounts,
                                  InstanceFactory<R, AttributesMap, L> instanceFactory,
                                  Integer minimumCount) {
        this.attributeProductCounts = attributeProductCounts;
        this.instanceFactory = instanceFactory;
        this.minimumCount = minimumCount;
        reduceMapSize();
    }

    private void reduceMapSize(){
        for (Map.Entry<String, Integer> entry : attributeProductCounts.entrySet()) {
            if (attributeProductCounts.get(entry.getKey()) <minimumCount ) {
                attributeProductCounts.remove(entry.getKey());
            }
        }
    }

    @Override
    public R transformInstance(I instance) {
        AttributesMap attributesMap = AttributesMap.newHashMap();
        AttributesMap rawAttributes = instance.getAttributes();
        for (Map.Entry<String, Serializable> entry : rawAttributes.entrySet()) {
            attributesMap.put(entry.getKey(), entry.getValue());
        }
        List<String> keys = null;//InstanceTransformerUtils.getOrderedKeys(instance);
        for (int i = 0; i < keys.size() - 1; i++) {
            String firstKey = keys.get(i);
            for (int j = i + 1; j < keys.size(); j++) {
                String secondKey = keys.get(j);
                String key = firstKey + "-" + secondKey;

                if (attributeProductCounts.containsKey(key) && attributeProductCounts.get(key) >= minimumCount) {
                    Number firstValue = (Number) attributesMap.get(firstKey);
                    Number secondValue = (Number) attributesMap.get(secondKey);
                    attributesMap.put(key, firstValue.doubleValue() * secondValue.doubleValue());
                }
            }
        }

        return instanceFactory.createInstance(attributesMap, instance.getLabel(), instance.getWeight());
    }
}
