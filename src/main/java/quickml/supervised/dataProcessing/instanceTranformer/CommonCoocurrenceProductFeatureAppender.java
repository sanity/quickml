package quickml.supervised.dataProcessing.instanceTranformer;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import quickml.data.instances.InstanceWithAttributesMap;

import java.util.*;


/**
 * Created by chrisreeves on 10/14/15.
 */
public class CommonCoocurrenceProductFeatureAppender<I extends InstanceWithAttributesMap> implements ProductFeatureAppender<I>{
    int minObservationsOfRawAttribute;
    int minOverlap;
    boolean approximateOverlap;
    boolean allowCategoricalProductFeatures;
    boolean allowNumericProductFeatures;
    private boolean ignoreAttributesCommonToAllInsances = false;


    public CommonCoocurrenceProductFeatureAppender setIgnoreAttributesCommonToAllInsances(boolean ignoreAttributesCommonToAllInsances) {
        this.ignoreAttributesCommonToAllInsances = ignoreAttributesCommonToAllInsances;
        return this;
    }


    public CommonCoocurrenceProductFeatureAppender setMinObservationsOfRawAttribute(int minObservationsOfRawAttribute) {
        this.minObservationsOfRawAttribute = minObservationsOfRawAttribute;
        return this;
    }

    public CommonCoocurrenceProductFeatureAppender setMinOverlap(int minOverlap) {
        this.minOverlap = minOverlap;
        return this;

    }

    public CommonCoocurrenceProductFeatureAppender setApproximateOverlap(boolean approximateOverlap) {
        this.approximateOverlap = approximateOverlap;
        return this;

    }

    public CommonCoocurrenceProductFeatureAppender setAllowCategoricalProductFeatures(boolean allowCategoricalProductFeatures) {
        this.allowCategoricalProductFeatures = allowCategoricalProductFeatures;
        return this;

    }

    public CommonCoocurrenceProductFeatureAppender setAllowNumericProductFeatures(boolean allowNumericProductFeatures) {
        this.allowNumericProductFeatures = allowNumericProductFeatures;
        return this;

    }
    @Override
    public List<I> addProductAttributes(List<I> trainingData) {
        Set<String> pairableAttributes = getPairableAttributes(trainingData, minObservationsOfRawAttribute, allowCategoricalProductFeatures, allowNumericProductFeatures);
        Map<String, List<Integer>> invertedIndex = buildInvertedIndexOfAttributesToInstances(trainingData, pairableAttributes);

        List<String> orderedPairableKeys = Lists.newArrayList(pairableAttributes);
        Collections.sort(orderedPairableKeys);

        for (int i = 0; i < orderedPairableKeys.size(); i++) {
            for (int j = i + 1; j < orderedPairableKeys.size(); j++) {
                String attribute1 = orderedPairableKeys.get(i);
                String attribute2 = orderedPairableKeys.get(j);
                if (attribute1.split("--")[0].equals(attribute2.split("--")[0])) {
                    continue; //skip self association of same attribute with different vals
                }
                List<Integer> instances1 = invertedIndex.get(attribute1);
                List<Integer> instances2 = invertedIndex.get(attribute2);
                if (enoughOverlap(instances1, instances2, minOverlap, trainingData.size(), approximateOverlap, ignoreAttributesCommonToAllInsances)) {
                    appendCrossAttributeToCommonInstances(trainingData, attribute1, attribute2, instances1, instances2);
                }
            }
        }
        return trainingData;
    }

    private static <I extends InstanceWithAttributesMap> Map<String, List<Integer>> buildInvertedIndexOfAttributesToInstances(List<I> trainingData, Set<String> pairableAttributes) {
        Map<String, List<Integer>> invertedIndex = new HashMap<>();
        for (int i = 0; i < trainingData.size(); i++) {
            I instance = trainingData.get(i);
            for (String key : instance.getAttributes().keySet()) {
                if (pairableAttributes.contains(key) && ((Double)instance.getAttributes().get(key)).doubleValue() != 0.0) {
                    if (!invertedIndex.containsKey(key)) {
                        invertedIndex.put(key, new ArrayList<Integer>());
                    }

                    List<Integer> instancesIn = invertedIndex.get(key);
                    instancesIn.add(i);
                    invertedIndex.put(key, instancesIn);
                }
            }
        }
        return invertedIndex;
    }

    private static <I extends InstanceWithAttributesMap> Set<String> getPairableAttributes(List<I> trainingData, int minObservationsOfRawAttribute, boolean allowCategoricalProductFeatures, boolean allowNumericProductFeatures) {
        Map<String, Integer> attributeCounts = new HashMap<>();
        Set<String> numericAttributes = Sets.newHashSet();
        for (I instance : trainingData) {
            for (String key : instance.getAttributes().keySet()) {
                if (!attributeCounts.containsKey(key)) {
                    attributeCounts.put(key, 0);
                }
                if (!instance.getAttributes().get(key).equals(1.0) && !instance.getAttributes().get(key).equals(0.0) ) {
                    numericAttributes.add(key);
                }
                attributeCounts.put(key, attributeCounts.get(key) + 1);
            }
        }

        Set<String> pairableAttributes = Sets.newHashSet();
        for (Map.Entry<String, Integer> entry : attributeCounts.entrySet()) {
            if (entry.getValue() > minObservationsOfRawAttribute) {
                if (allowNumericProductFeatures && numericAttributes.contains(entry.getKey())) {
                    pairableAttributes.add(entry.getKey());
                }
                if (allowCategoricalProductFeatures && !numericAttributes.contains(entry.getKey())) {
                    pairableAttributes.add(entry.getKey());
                }
            }
        }
        return pairableAttributes;
    }

    private static Set<String> identifyPairableAttributes(int minObservationsOfRawAttribute, Map<String, Integer> attributeCounts) {
        Set<String> pairableAttributes = Sets.newHashSet();
        for (Map.Entry<String, Integer> entry : attributeCounts.entrySet()) {
            if (entry.getValue() > minObservationsOfRawAttribute) {
                pairableAttributes.add(entry.getKey());
            }
        }
        return pairableAttributes;
    }

    private static boolean enoughOverlap(List<Integer> instances1, List<Integer> instances2, int minOverlap, int numInstances, boolean approximateOverlap, boolean ignoreAttributesCommonToAllInsances) {
        int overlap = 0;
        if (ignoreAttributesCommonToAllInsances && (instances1.size() == numInstances || instances2.size() == numInstances)) {
            return false;
        }
        if (approximateOverlap && instances1.size()> numInstances/4 || instances2.size() > numInstances/4) {
            int larger = Math.max(instances1.size(), instances2.size());
            int lesser = Math.min(instances1.size(), instances2.size());
            overlap = larger/numInstances * lesser;
        }

        else {
            int index1 = 0, index2 = 0;
            while (index1 < instances1.size() && index2 < instances2.size()) {
                if (instances1.get(index1).intValue() == instances2.get(index2).intValue()) {
                    overlap++;
                    index1++;
                    index2++;
                    if (overlap >= minOverlap) {
                        return true;
                    }
                } else if (instances1.get(index1).intValue() < instances2.get(index2).intValue()) {
                    index1++;
                } else {
                    index2++;
                }
                int remainingOverlap = minOverlap - overlap;
                if (remainingOverlap > instances2.size() -index2 || remainingOverlap > instances1.size() -index1 ) {
                    return false;
                }
            }
        }
        return overlap >= minOverlap;
    }

    private static <I extends InstanceWithAttributesMap> List<I> appendCrossAttributeToCommonInstances(List<I> trainingData, String attribute1, String attribute2, List<Integer> instances1, List<Integer> instances2) {
        int index1 = 0, index2 = 0;
        String newAttribute = attribute1 + "-" + attribute2;
        while (index1<instances1.size() && index2 < instances2.size()){
            if (instances1.get(index1).intValue() == instances2.get(index2).intValue()) {
                I instance = trainingData.get(instances1.get(index1).intValue());
                double val1 = (Double) instance.getAttributes().get(attribute1);
                double val2 = (Double) instance.getAttributes().get(attribute2);
                instance.getAttributes().put(newAttribute, val1 * val2);
                index1++;
                index2++;
            }
            else if (instances1.get(index1).intValue() < instances2.get(index2).intValue()) {
                index1++;
            }
            else {
                index2++;
            }
        }
        return trainingData;
    }

}
