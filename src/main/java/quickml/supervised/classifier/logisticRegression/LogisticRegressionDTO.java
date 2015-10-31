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
public class LogisticRegressionDTO<I extends SparseClassifierInstance, D extends LogisticRegressionDTO<I, D>> extends TransformedDataWithDates<I, D>{

    private List<I> instances;
    private HashMap<String, Integer> nameToIndexMap;
    private Map<String, Utils.MeanStdMaxMin> meanStdMaxMins;
    private Map<Serializable, Double> numericClassLabels;
    private DateTimeExtractor<I> dateTimeExtractor;

    public LogisticRegressionDTO DateTimeExtractor(DateTimeExtractor<I> dateTimeExtractor) {
        this.dateTimeExtractor = dateTimeExtractor;
        return this;
    }

    @Override
    public List<I> getTransformedInstances() {
        return instances;
    }

    @Override
    public D copyWithJustTraniningSet(List<I> trainingSet) {
        return (D)new LogisticRegressionDTO(trainingSet, nameToIndexMap, meanStdMaxMins, numericClassLabels);
    }

    public HashMap<String, Integer> getNameToIndexMap() {
        return nameToIndexMap;
    }

    public Map<String, Utils.MeanStdMaxMin> getMeanStdMaxMins() {
        return meanStdMaxMins;
    }

    public Map<Serializable, Double> getNumericClassLabels() {
        return numericClassLabels;
    }

    public DateTimeExtractor<I> getDateTimeExtractor() {
        if (dateTimeExtractor!= null) {
            return dateTimeExtractor;
        }
        else if (meanStdMaxMins == null) {
            return new OnespotDateTimeExtractor<>();
        } else {
            return new MeanNormalizedDateTimeExtractor<>(meanStdMaxMins);
        }
    }

    public LogisticRegressionDTO(List<I> instances,
                                 HashMap<String, Integer> nameToIndexMap,
                                 Map<String, Utils.MeanStdMaxMin> meanStdMaxMins,
                                 Map<Serializable, Double> numericClassLabels) {
        this.instances = instances;
        this.nameToIndexMap = nameToIndexMap;
        this.meanStdMaxMins = meanStdMaxMins;
        this.numericClassLabels = numericClassLabels;
    }

    public LogisticRegressionDTO(List<I> instances) {
        this.instances = instances;
    }

}
