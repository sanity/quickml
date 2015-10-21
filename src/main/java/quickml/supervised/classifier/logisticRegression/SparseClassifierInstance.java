package quickml.supervised.classifier.logisticRegression;

import javafx.util.Pair;
import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class SparseClassifierInstance extends ClassifierInstance {
    private int[] indicesOfCorrespondingWeights;
    private double[] values;

    public SparseClassifierInstance(AttributesMap attributes, Serializable label, Map<String, Integer> nameToValueIndexMap) {
        super(attributes, label);
        setIndicesAndValues(attributes, nameToValueIndexMap);
    }

    public SparseClassifierInstance(AttributesMap attributes, Serializable label, double weight, Map<String, Integer> nameToValueIndexMap) {
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
