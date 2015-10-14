package quickml.supervised.tree.dataProcessing;

import quickml.data.AttributesMap;
import quickml.data.instances.InstanceFactory;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.Utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class MeanNormalizeAllNumericAttributes<L extends Serializable, I extends InstanceWithAttributesMap<L>, R extends InstanceWithAttributesMap<L>> implements InstanceTransformer<I, R>{


    private Map<String, Utils.MeanStdMaxMin> meansAndStds;
    private Map<String,AttributeCharacteristics> attributeCharacteristics;
    private InstanceFactory<R, AttributesMap, L> instanceFactory;

    public MeanNormalizeAllNumericAttributes(List<I> trainingData, Map<String, AttributeCharacteristics> attributeCharacteristics, InstanceFactory<R, AttributesMap, L> instanceFactory) {
        this.meansAndStds = Utils.<I>getMeanStdMaxMins(attributeCharacteristics, trainingData);
        this.attributeCharacteristics = attributeCharacteristics;
        this.instanceFactory = instanceFactory;
    }





    @Override
    public R transformInstance(I instance) {
            AttributesMap attributesMap = AttributesMap.newHashMap();
            AttributesMap rawAttributes = instance.getAttributes();

            //1,0 normalize?
            for (String key : rawAttributes.keySet()) {
                if (attributeCharacteristics.get(key).isNumber) {
                    Utils.MeanStdMaxMin meanStdMaxMin = meansAndStds.get(key);
                    attributesMap.put(key, meanNormalize(rawAttributes, key, meanStdMaxMin));
                } else {
                    attributesMap.put(key, rawAttributes.get(key));
                }
            }
            return instanceFactory.createInstance(attributesMap, instance.getLabel(), instance.getWeight());
    }

    public static double meanNormalize(AttributesMap rawAttributes, String key, Utils.MeanStdMaxMin meanStdMaxMin) {
        return (((Number) rawAttributes.get(key)).doubleValue() - meanStdMaxMin.getMean()) / meanStdMaxMin.getNonZeroStd();
    }
}
