package quickml.supervised.classifier.logisticRegression;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class InstanceTransformerUtils {

    public static HashMap<String, Integer> populateNameToIndexMap(List<SparseClassifierInstance> trainingData) {
        HashMap<String, Integer> nameToIndexMap = Maps.newHashMap();
        int index = 0;
        for (SparseClassifierInstance instance : trainingData) {
            for (String key : instance.getAttributes().keySet()) {
                if (!nameToIndexMap.containsKey(key)) {
                    nameToIndexMap.put(key, index);
                    index++;
                }
            }
        }
        return nameToIndexMap;
    }

    public static Map<Serializable, Double> getNumericClassLabels(List<SparseClassifierInstance> trainingData) {
        Map<Serializable, Double> classifications = Maps.newHashMap();
        double numericClassRepresentation = 0.0;
        for (SparseClassifierInstance instance : trainingData) {
            if (!classifications.containsKey(instance.getLabel())) {
                classifications.put(instance.getLabel(), numericClassRepresentation);
                numericClassRepresentation += 1.0;
            }

        }
        return classifications;
    }

    public static String oneHotEncode(String attributeName, Serializable attributeValue) {
        return attributeName + "-" + attributeValue;
    }

}
