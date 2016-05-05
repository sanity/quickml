package quickml.supervised.regressionModel.LinearRegression2;

import quickml.data.OnespotDateTimeExtractor;
import quickml.data.instances.SparseRegressionInstance;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;
import quickml.supervised.crossValidation.utils.MeanNormalizedDateTimeExtractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/28/15.
 */
public  class MeanNormalizedAndDatedLinearRegressionDTO extends LinearRegressionDTO<MeanNormalizedAndDatedLinearRegressionDTO> {


    private Map<String, Utils.MeanStdMaxMin> meanStdMaxMins;
    private DateTimeExtractor<SparseRegressionInstance> dateTimeExtractor;

    public MeanNormalizedAndDatedLinearRegressionDTO DateTimeExtractor(DateTimeExtractor<SparseRegressionInstance> dateTimeExtractor) {
        this.dateTimeExtractor = dateTimeExtractor;
        return this;
    }


    @Override
    public MeanNormalizedAndDatedLinearRegressionDTO copyWithJustTrainingSet(List<SparseRegressionInstance> trainingSet) {
        return new MeanNormalizedAndDatedLinearRegressionDTO(trainingSet, nameToIndexMap, meanStdMaxMins);
    }


    public Map<String, Utils.MeanStdMaxMin> getMeanStdMaxMins() {
        return meanStdMaxMins;
    }


    public DateTimeExtractor<SparseRegressionInstance> getDateTimeExtractor() {
        if (dateTimeExtractor!= null) {
            return dateTimeExtractor;
        }
        else if (meanStdMaxMins == null) {
            return new OnespotDateTimeExtractor<>();
        } else {
            return new MeanNormalizedDateTimeExtractor<>(meanStdMaxMins);
        }
    }

    public MeanNormalizedAndDatedLinearRegressionDTO(List<SparseRegressionInstance> instances,
                                                     HashMap<String, Integer> nameToIndexMap,
                                                     Map<String, Utils.MeanStdMaxMin> meanStdMaxMins
                                                    ) {
        super(instances, nameToIndexMap);
        this.meanStdMaxMins = meanStdMaxMins;
    }

    public MeanNormalizedAndDatedLinearRegressionDTO(List<SparseRegressionInstance> instances) {
        super(instances);
    }

}
