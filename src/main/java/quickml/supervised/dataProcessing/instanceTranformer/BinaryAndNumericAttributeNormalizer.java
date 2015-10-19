package quickml.supervised.dataProcessing.instanceTranformer;

import quickml.data.AttributesMap;
import quickml.data.instances.InstanceFactory;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.Utils;
import quickml.supervised.dataProcessing.BinaryAttributeCharacteristics;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class BinaryAndNumericAttributeNormalizer<L extends Serializable, I extends InstanceWithAttributesMap<L>, R extends InstanceWithAttributesMap<L>> implements InstanceTransformer<I, R> {

/**class does min-max normalization of binary attributes and mean normalization of non binary attributes.  All attributes are assumed to have
 * numeric values*/
    private Map<String, Utils.MeanStdMaxMin> meanStdMaxMins;
    private Map<String,BinaryAttributeCharacteristics> binaryAttributeCharacteristics;
    private InstanceFactory<R, AttributesMap, L> instanceFactory;
    private NoNormalizationCondition noNormalizationCondition;


    public BinaryAndNumericAttributeNormalizer(List<I> trainingData, InstanceFactory<R, AttributesMap, L> instanceFactory, NoNormalizationCondition noNormalizationCondition) {
        this.noNormalizationCondition = noNormalizationCondition;
        this.meanStdMaxMins = Utils.<I>getMeanStdMaxMins(trainingData);
        this.binaryAttributeCharacteristics = Utils.<I>getMapOfAttributesToBinaryAttributeCharacteristics(trainingData);
        this.instanceFactory = instanceFactory;

    }

    public Map<String, BinaryAttributeCharacteristics> getBinaryAttributeCharacteristics() {
        return binaryAttributeCharacteristics;
    }

    public Map<String, Utils.MeanStdMaxMin> getMeanStdMaxMins() {
        return meanStdMaxMins;
    }

    @Override
    public R transformInstance(I instance) {
            AttributesMap attributesMap = AttributesMap.newHashMap();
            AttributesMap rawAttributes = instance.getAttributes();

            //1,0 normalize?
            for (String key : rawAttributes.keySet()) {
                Utils.MeanStdMaxMin meanStdMaxMin = meanStdMaxMins.get(key);
                if (binaryAttributeCharacteristics.get(key).getIsBinary() && !noNormalizationCondition.noNormalization(key)) {
                    attributesMap.put(key, minMaxNormalize(rawAttributes, key, meanStdMaxMin));

                } else if (!noNormalizationCondition.noNormalization(key)) {
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
    public static double minMaxNormalize(AttributesMap rawAttributes, String key, Utils.MeanStdMaxMin meanStdMaxMin) {
        return (((Number) rawAttributes.get(key)).doubleValue()) / meanStdMaxMin.getMaxMinMinusMin();
    }

public interface NoNormalizationCondition {
    boolean noNormalization(String key);
}
}
