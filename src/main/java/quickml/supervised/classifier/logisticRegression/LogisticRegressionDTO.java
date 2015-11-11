package quickml.supervised.classifier.logisticRegression;

import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;
import quickml.supervised.crossValidation.utils.MeanNormalizedDateTimeExtractor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/28/15.
 */
public  abstract class LogisticRegressionDTO<D extends LogisticRegressionDTO<D>> implements TransformedDataWithDates<SparseClassifierInstance, D> {

    protected List<SparseClassifierInstance> instances;
    protected HashMap<String, Integer> nameToIndexMap;
    protected Map<Serializable, Double> numericClassLabels;


    @Override
    public List<SparseClassifierInstance> getTransformedInstances() {
        return instances;
    }

    public HashMap<String, Integer> getNameToIndexMap() {
        return nameToIndexMap;
    }


    public Map<Serializable, Double> getNumericClassLabels() {
        return numericClassLabels;
    }



    public LogisticRegressionDTO(List<SparseClassifierInstance> instances,
                                 HashMap<String, Integer> nameToIndexMap,
                                 Map<Serializable, Double> numericClassLabels) {
        this.instances = instances;
        this.nameToIndexMap = nameToIndexMap;
        this.numericClassLabels = numericClassLabels;
    }

    public LogisticRegressionDTO(List<SparseClassifierInstance> instances) {
        this.instances = instances;
    }

}
