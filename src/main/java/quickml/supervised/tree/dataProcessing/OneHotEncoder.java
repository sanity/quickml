package quickml.supervised.tree.dataProcessing;

import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.data.InstanceFactory;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.Utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.logRegression.InstanceTransformerUtils.oneHotEncode;
import static quickml.supervised.classifier.logRegression.LogisticRegressionBuilder.meanNormalize;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class OneHotEncoder<I extends InstanceWithAttributesMap<?>, R extends InstanceWithAttributesMap<?>> implements InstanceTransformer<I, R> {
    Map<String, AttributeCharacteristics> attributeCharacteristics;
    InstanceFactory<R, AttributesMap, Serializable> instanceFactory;

    public OneHotEncoder(Map<String, AttributeCharacteristics> attributeCharacteristics) {
        this.attributeCharacteristics = attributeCharacteristics;
    }

    @Override
    public R transformInstance(I instance) {
        AttributesMap attributesMap = AttributesMap.newHashMap();
        AttributesMap rawAttributes = instance.getAttributes();
        for (String key : rawAttributes.keySet()) {
            if (!attributeCharacteristics.get(key).isNumber) {
                attributesMap.put(oneHotEncode(key, rawAttributes.get(key)), 1.0);
            }
        }
        return instanceFactory.createInstance(attributesMap, instance.getLabel(), instance.getWeight());
    }

    private void meanNormalizeAndOneHotEncode(List<ClassifierInstance> trainingData, Map<String, AttributeCharacteristics> attributeCharacteristics,
                                              List<ClassifierInstance> normalizedInstances, Map<String, Utils.MeanAndStd> meansAndStds) {
        for (ClassifierInstance instance : trainingData) {
            AttributesMap attributesMap = AttributesMap.newHashMap();
            AttributesMap rawAttributes = instance.getAttributes();
            for (String key : rawAttributes.keySet()) {
                if (attributeCharacteristics.get(key).isNumber) {
                    Utils.MeanAndStd meanAndStd = meansAndStds.get(key);
                    attributesMap.put(key, meanNormalize(rawAttributes, key, meanAndStd));
                } else {
                    attributesMap.put(oneHotEncode(key, rawAttributes.get(key)), 1.0);
                }
            }
            normalizedInstances.add(new ClassifierInstance(attributesMap, instance.getLabel()));
        }
    }
}
