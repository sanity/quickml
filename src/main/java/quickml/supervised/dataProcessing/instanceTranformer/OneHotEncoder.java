package quickml.supervised.dataProcessing.instanceTranformer;

import quickml.data.AttributesMap;
import quickml.data.instances.InstanceFactory;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.dataProcessing.AttributeCharacteristics;

import java.io.Serializable;
import java.util.Map;

import static quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils.oneHotEncode;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class OneHotEncoder<L extends Serializable, I extends InstanceWithAttributesMap<L>, R extends InstanceWithAttributesMap<Serializable>> implements InstanceTransformer<I, R> {
    final Map<String, AttributeCharacteristics> attributeCharacteristics;
    final InstanceFactory<R, AttributesMap, L> instanceFactory;

    public OneHotEncoder(Map<String, AttributeCharacteristics> attributeCharacteristics, final InstanceFactory<R, AttributesMap, L> instanceFactory) {
        this.attributeCharacteristics = attributeCharacteristics;
        this.instanceFactory = instanceFactory;
    }

    @Override
    public R transformInstance(I instance) {
        AttributesMap attributesMap = AttributesMap.newHashMap();
        AttributesMap rawAttributes = instance.getAttributes();
        for (String key : rawAttributes.keySet()) {
            if (!attributeCharacteristics.get(key).isNumber) {
                attributesMap.put(oneHotEncode(key, rawAttributes.get(key)), 1.0);
            }
            else {
                attributesMap.put(key, ((Number)rawAttributes.get(key)).doubleValue());
            }
        }
        return instanceFactory.createInstance(attributesMap, instance.getLabel(), instance.getWeight());
    }
}
