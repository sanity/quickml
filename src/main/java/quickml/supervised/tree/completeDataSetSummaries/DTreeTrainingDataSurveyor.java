package quickml.supervised.tree.completeDataSetSummaries;

import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.constants.BranchType;

import java.util.*;

/**
 * Created by alexanderhawk on 3/19/15.
 */
public class DTreeTrainingDataSurveyor<T extends ClassifierInstance> {

    private boolean considerBooleanAttributes = false;

    public DTreeTrainingDataSurveyor(boolean considerBooleanAttributes) {
        this.considerBooleanAttributes = considerBooleanAttributes;
    }

    public Map<BranchType, Set<String>> groupAttributesByType(final List<T> trainingData) {
        Map<String, AttributeCharacteristics> attributeCharacteristics = getMapOfAttributesToAttributeCharacteristics(trainingData);
        Map<BranchType, Set<String>> attributesByType = groupByType(attributeCharacteristics);
        return attributesByType;
    }

    private Map<String, AttributeCharacteristics> getMapOfAttributesToAttributeCharacteristics(List<T> trainingData) {
        Map<String, AttributeCharacteristics> attributeCharacteristics = Maps.newHashMap();

        for (T instance : trainingData) {
            for (Map.Entry<String, Object> e : instance.getAttributes().entrySet()) {
                AttributeCharacteristics attributeCharacteristic = attributeCharacteristics.get(e.getKey());
                if (attributeCharacteristic == null) {
                    attributeCharacteristic = new AttributeCharacteristics();
                    attributeCharacteristics.put(e.getKey(), attributeCharacteristic);
                }
                if (!(e.getValue() instanceof Number)) {
                    attributeCharacteristic.isNumber = false;
                }
                attributeCharacteristic.updateBooleanStatus(e.getValue());
            }
        }
        return attributeCharacteristics;
    }


    private Map<BranchType, Set<String>> groupByType(Map<String, AttributeCharacteristics> attributeCharacteristics) {
        Map<BranchType, Set<String>> attributesByType = Maps.newHashMap();
        attributesByType.put(BranchType.CATEGORICAL, new HashSet<String>());
        attributesByType.put(BranchType.NUMERIC, new HashSet<String>());
        if (considerBooleanAttributes)
            attributesByType.put(BranchType.BOOLEAN, new HashSet<String>());

        for(String attribute : attributeCharacteristics.keySet()) {
            if (attributeCharacteristics.get(attribute).isNumber) {
                attributesByType.get(BranchType.NUMERIC).add(attribute);
            }   else if (considerBooleanAttributes && attributeCharacteristics.get(attribute).isBoolean) {
                attributesByType.get(BranchType.BOOLEAN).add(attribute);
            }   else {
                attributesByType.get(BranchType.CATEGORICAL).add(attribute);
            }
        }
        return attributesByType;
    }


    public static class AttributeCharacteristics {

        public boolean isNumber = true;
        public boolean isBoolean = true;
        private TreeSet<Object> observedVals = new TreeSet();

        public void updateBooleanStatus(Object val) {
            if (!isBoolean || val == null) {
                return;
            }
            if (observedVals.size() == 2 && !observedVals.contains(val)) {
                isBoolean = false;
            } else {
                observedVals.add(val);
            }
            if (bothValsAreNumbers()) {
                isBoolean = false;
            }
        }

        private boolean bothValsAreNumbers() {
            return observedVals.size() == 2 && observedVals.first() instanceof Number && observedVals.last() instanceof Number;
        }
    }
}
