package quickml.supervised.classifier.logRegression;

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

    public static Map<String, Integer> getAttributeProductCounts(List<? extends InstanceWithAttributesMap> trainingData) {
        Map<String, Integer> map = new HashMap<>();
        for (InstanceWithAttributesMap instance : trainingData) {
            List<String> keys = getOrderedKeys(instance);
            for (int i = 0; i < keys.size() - 1; i++) {
                String firstKey = keys.get(i);
                if (instance.getAttributes().get(firstKey).equals(0.0)) {
                    continue;
                }
                for (int j = i + 1; j < keys.size(); j++) {
                    String secondKey = keys.get(j);
                    if (instance.getAttributes().get(secondKey).equals(0.0)) {
                        continue;
                    }
                    String key = firstKey + "-" + secondKey;
                    Integer count = map.get(key);
                    if (count == null) {
                        count = 0;
                    }
                    count++;
                    map.put(key, count);
                }
            }
        }
        return map;
    }

    public static List<String> getOrderedKeys(InstanceWithAttributesMap instance) {
        List<String> keys = Lists.newArrayList(instance.getAttributes().keySet());
        Collections.sort(keys);
        return keys;
    }

    public static <T extends ClassifierInstance> HashMap<String, Integer> populateNameToIndexMap(List<T> trainingData) {
        HashMap<String, Integer> nameToIndexMap = Maps.newHashMap();
        int index = 0;
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

    public static <T extends InstanceWithAttributesMap> Map<Serializable, Double> determineNumericClassLabels(List<T> trainingData) {

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

    public static <T extends ClassifierInstance>  Set<Double>  getClassifications(List<T> trainingData) {
        Set<Double> classifications = Sets.newHashSet();
        for (T instance : trainingData) {
            if (!(instance.getLabel() instanceof Double)) {
                throw new RuntimeException("must have numeric features");
            }
            classifications.add((Double)instance.getLabel());
        }
        return classifications;
    }

    public static String oneHotEncode(String attributeName, Serializable attributeValue) {
        return attributeName + "-" + attributeValue;
    }

}
