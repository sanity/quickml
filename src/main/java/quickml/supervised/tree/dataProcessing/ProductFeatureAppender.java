package quickml.supervised.tree.dataProcessing;

import quickml.data.AttributesMap;
import quickml.data.InstanceFactory;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.logRegression.InstanceTransformerUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * Created by chrisreeves on 10/14/15.
 */
public class ProductFeatureAppender<L extends Number, I extends InstanceWithAttributesMap<L>, R extends InstanceWithAttributesMap<L>> implements InstanceTransformer<I, R> {
    private final Map<String, Integer> attributeProductCounts;
    private final InstanceFactory<R, AttributesMap, Serializable> instanceFactory;
    private final Integer minimumCount;

    public ProductFeatureAppender(Map<String, Integer> attributeProductCounts,
                                  InstanceFactory<R, AttributesMap, Serializable> instanceFactory,
                                  Integer minimumCount) {
        this.attributeProductCounts = attributeProductCounts;
        this.instanceFactory = instanceFactory;
        this.minimumCount = minimumCount;
    }


    @Override
    public R transformInstance(I instance) {
        AttributesMap attributesMap = AttributesMap.newHashMap();
        AttributesMap rawAttributes = instance.getAttributes();
        for (Map.Entry<String, Serializable> entry : rawAttributes.entrySet()) {
            attributesMap.put(entry.getKey(), entry.getValue());
        }
        List<String> keys = InstanceTransformerUtils.getOrderedKeys(instance);
        for (int i = 0; i < keys.size() - 1; i++) {
            String firstKey = keys.get(i);
            for (int j = i + 1; j < keys.size(); j++) {
                String secondKey = keys.get(j);
                String key = firstKey + "-" + secondKey;

                if (attributeProductCounts.get(key) > minimumCount) {
                    Number firstValue = (Number) attributesMap.get(firstKey);
                    Number secondValue = (Number) attributesMap.get(secondKey);
                    attributesMap.put(key, firstValue.doubleValue() * secondValue.doubleValue());
                }
            }
        }

        return instanceFactory.createInstance(attributesMap, instance.getLabel(), instance.getWeight());
    }
}
