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

    public static <T extends InstanceWithAttributesMap> HashMap<String, Integer> populateNameToIndexMap
            (List<T> trainingData, boolean useBias) {
        HashMap<String, Integer> nameToIndexMap = Maps.newHashMap();
        int index = 0;
        if (useBias) {
            nameToIndexMap.put(BIAS_TERM, index);
            index++;
        }
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
