package quickml.supervised.classifier.logisticRegression;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.InstanceWithAttributesMap;

import java.io.Serializable;
import java.util.*;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class InstanceTransformerUtils {

    public static final String BIAS_TERM = "biasTerm";

    public static <I extends InstanceWithAttributesMap> List<I> addProductAttributes(List<I> trainingData, int minObservationsOfRawAttribute, int minOverlap, boolean approximateOverlap) {
        Set<String> pairableAttributes = getPairableAttributes(trainingData, minObservationsOfRawAttribute);
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
                if (enoughOverlap(instances1, instances2, minOverlap, trainingData.size(), approximateOverlap)) {
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

    private static <I extends InstanceWithAttributesMap> Set<String> getPairableAttributes(List<I> trainingData, int minObservationsOfRawAttribute) {
        Map<String, Integer> attributeCounts = new HashMap<>();
        Set<String> numericAttributes = Sets.newHashSet();
        for (I instance : trainingData) {
            for (String key : instance.getAttributes().keySet()) {
                if (!attributeCounts.containsKey(key)) {
                    attributeCounts.put(key, 0);
                }
                if (!instance.getAttributes().get(key).equals(1.0) || !instance.getAttributes().get(key).equals(0.0) ) {
                    numericAttributes.add(key);
                }
                attributeCounts.put(key, attributeCounts.get(key) + 1);
                }
            }

        Set<String> pairableAttributes = Sets.newHashSet();
        for (Map.Entry<String, Integer> entry : attributeCounts.entrySet()) {
            if (entry.getValue() > minObservationsOfRawAttribute) {
                pairableAttributes.add(entry.getKey());
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

    private static boolean enoughOverlap(List<Integer> instances1, List<Integer> instances2, int minOverlap, int numInstances, boolean approximateOverlap) {
        int overlap = 0;
        if (instances1.size() == numInstances || instances2.size() == numInstances)
            return false;
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
                    if (overlap > minOverlap) {
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
        return overlap > minOverlap;
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


    public static <T extends ClassifierInstance> HashMap<String, Integer> populateNameToIndexMap
            (List<T> trainingData) {
        HashMap<String, Integer> nameToIndexMap = Maps.newHashMap();
        int index = 0;
        nameToIndexMap.put(BIAS_TERM, index);
        index++;
        for (T instance : trainingData) {
            for (String key : instance.getAttributes().keySet()) {
                if (!nameToIndexMap.containsKey(key)) {
                    nameToIndexMap.put(key, index);
                    index++;
                }
            }
        }
        return nameToIndexMap;
    }

    public static <T extends InstanceWithAttributesMap> Map<Serializable, Double> determineNumericClassLabels
            (List<T> trainingData) {

        /**class identifies a map from instances to numeric values;*/
        Map<Serializable, Double> classifications = Maps.newHashMap();
        if (hasOneZeroLabels(trainingData)) {
            classifications.put(1.0, 1.0);
            classifications.put(0.0, 0.0);
            return classifications;
        }
        double numericClassRepresentation = 0.0;
        for (T instance : trainingData) {
            if (!classifications.containsKey(instance.getLabel())) {
                classifications.put(instance.getLabel(), numericClassRepresentation);
                numericClassRepresentation += 1.0;
            }

        }
        return classifications;
    }

    private static <T extends InstanceWithAttributesMap> boolean hasOneZeroLabels(List<T> trainingData) {
        for (T instance : trainingData) {
            if (!instance.getLabel().equals(Double.valueOf(1.0)) &&
                    !instance.getLabel().equals(Double.valueOf(0.0))) {
                return false;
            }
        }
        return true;
    }

    public static <T extends ClassifierInstance> Set<Double> getClassifications(List<T> trainingData) {
        Set<Double> classifications = Sets.newHashSet();
        for (T instance : trainingData) {
            if (!(instance.getLabel() instanceof Double)) {
                throw new RuntimeException("must have numeric features");
            }
            classifications.add((Double) instance.getLabel());
        }
        return classifications;
    }

    public static String oneHotEncode(String attributeName, Serializable attributeValue) {
        return attributeName + "--" + attributeValue;
    }

}
