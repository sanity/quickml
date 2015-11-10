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
public class MeanNormalizedAndDatedLogisticRegressionDTO extends LogisticRegressionDTO<MeanNormalizedAndDatedLogisticRegressionDTO> {


    private Map<String, Utils.MeanStdMaxMin> meanStdMaxMins;
    private DateTimeExtractor<SparseClassifierInstance> dateTimeExtractor;

    public MeanNormalizedAndDatedLogisticRegressionDTO DateTimeExtractor(DateTimeExtractor<SparseClassifierInstance> dateTimeExtractor) {
        this.dateTimeExtractor = dateTimeExtractor;
        return this;
    }


    @Override
    public MeanNormalizedAndDatedLogisticRegressionDTO copyWithJustTrainingSet(List<SparseClassifierInstance> trainingSet) {
        return new MeanNormalizedAndDatedLogisticRegressionDTO(trainingSet, nameToIndexMap, meanStdMaxMins, numericClassLabels);
    }


    public Map<String, Utils.MeanStdMaxMin> getMeanStdMaxMins() {
        return meanStdMaxMins;
    }


    public DateTimeExtractor<SparseClassifierInstance> getDateTimeExtractor() {
        if (dateTimeExtractor!= null) {
            return dateTimeExtractor;
        }
        else if (meanStdMaxMins == null) {
            return new OnespotDateTimeExtractor<>();
        } else {
            return new MeanNormalizedDateTimeExtractor<>(meanStdMaxMins);
        }
    }

    public MeanNormalizedAndDatedLogisticRegressionDTO(List<SparseClassifierInstance> instances,
                                                       HashMap<String, Integer> nameToIndexMap,
                                                       Map<String, Utils.MeanStdMaxMin> meanStdMaxMins,
                                                       Map<Serializable, Double> numericClassLabels) {
        super(instances, nameToIndexMap, numericClassLabels);
        this.meanStdMaxMins = meanStdMaxMins;
    }

    public MeanNormalizedAndDatedLogisticRegressionDTO(List<SparseClassifierInstance> instances) {
        super(instances);
    }

}
