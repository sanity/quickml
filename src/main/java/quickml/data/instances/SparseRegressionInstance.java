package quickml.data.instances;

import org.javatuples.Pair;
import quickml.data.AttributesMap;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class SparseRegressionInstance extends RegressionInstance {
    private int[] indicesOfCorrespondingWeights;
    private double[] values;

    public SparseRegressionInstance(AttributesMap attributes, Double label, Map<String, Integer> nameToValueIndexMap) {
        super(attributes, label);
        setIndicesAndValues(attributes, nameToValueIndexMap);
    }

    public SparseRegressionInstance(AttributesMap attributes, Double label, double weight, Map<String, Integer> nameToValueIndexMap) {
        super(attributes, label, weight);
        setIndicesAndValues(attributes, nameToValueIndexMap);
    }

    private void setIndicesAndValues(AttributesMap attributes, Map<String, Integer> nameToIndexMap) {
        indicesOfCorrespondingWeights = new int[attributes.size()+1];
        values = new double[attributes.size()+1];
        //add bias term
        indicesOfCorrespondingWeights[0] = 0;
        values[0] = 1.0;
        //add non bias terms
        int i = 1;
        for (Map.Entry<String, Serializable> entry : attributes.entrySet()) {
            if (!(entry.getValue() instanceof Double)) {
                throw new RuntimeException("wrong type of values in attributes");
            }
            int valueIndex = nameToIndexMap.get(entry.getKey());
            indicesOfCorrespondingWeights[i] = valueIndex;
            values[i] = (Double)entry.getValue();
            i++;
        }
    }

    public static double[] getArrayOfValues(RegressionInstance regressionInstance, Map<String, Integer> nameToIndexMap, boolean useBias){
        int numAttributes = regressionInstance.getAttributes().size();
        AttributesMap attributesMap = regressionInstance.getAttributes();
        double[] valuesArray;
        int attributeIndex = 0;

        if (useBias) {
            valuesArray = new double[numAttributes + 1];
            valuesArray[0] = 1.0;
            attributeIndex++;
        } else {
            valuesArray = new double[numAttributes];
        }
        for (Map.Entry<String, Serializable> attributeEntry : attributesMap.entrySet()) {
            attributeIndex = nameToIndexMap.get(attributeEntry.getKey());
            valuesArray[attributeIndex] = (Double)attributeEntry.getValue();
        }
        return valuesArray;
    }

    @Override
    public AttributesMap getAttributes() {
        return super.getAttributes();
    }

    public Pair<int[], double[]> getSparseAttributes(){
        return new Pair<>(indicesOfCorrespondingWeights, values);
    }

    public double dotProduct(double[] omega) {
        double result = 0;
        for (int i = 0; i< indicesOfCorrespondingWeights.length; i++) {
            int indexOfFeature = indicesOfCorrespondingWeights[i];
            double valueOfFeature = values[i];
            result+= omega[indexOfFeature]* valueOfFeature;
        }
        return result;
    }
}
