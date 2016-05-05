package quickml.data.instances;

import quickml.data.AttributesMap;
import quickml.supervised.classifier.logisticRegression.SparseClassifierInstance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/14/15.
 */
public class SparseClassifierInstanceFactory implements InstanceFactory<SparseClassifierInstance, AttributesMap, Serializable> {
    private Map<String, Integer> nameToIndexMap;

    public SparseClassifierInstanceFactory(Map<String, Integer> nameToIndexMap) {
        this.nameToIndexMap = nameToIndexMap;
    }


    @Override
    public SparseClassifierInstance createInstance(AttributesMap attributes, Serializable label, double weight) {
        return new SparseClassifierInstance(attributes, label, weight, nameToIndexMap);
    }
}
