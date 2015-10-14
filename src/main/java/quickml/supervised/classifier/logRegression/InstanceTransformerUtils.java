package quickml.supervised.classifier.logRegression;

import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class InstanceTransformerUtils {

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

    public static <T extends ClassifierInstance> Map<Serializable, Double> getNumericClassLabels(List<T> trainingData) {
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

    private static <T extends ClassifierInstance> boolean hasOneZeroLabels(List<T> trainingData) {
        for (T sparseClassifierInstance : trainingData) {
            if (!sparseClassifierInstance.getLabel().equals(Double.valueOf(1.0)) && !sparseClassifierInstance.getLabel().equals(Double.valueOf(0.0))) {
                return false;
            }
        }
        return true;
    }

    public static String oneHotEncode(String attributeName, Serializable attributeValue) {
        return attributeName + "-" + attributeValue;
    }

}
