package quickml.supervised.crossValidation;

import com.google.common.base.Optional;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class ClassifierOutOfTimeCrossValidator extends OutOfTimeCrossValidator<AttributesMap, PredictionMap> {
    public ClassifierOutOfTimeCrossValidator(CrossValLossFunction<PredictionMap> crossValLossFunction, double fractionOfDataForCrossValidation, int validationTimeSliceHours, DateTimeExtractor dateTimeExtractor) {
        super(crossValLossFunction, fractionOfDataForCrossValidation, validationTimeSliceHours, dateTimeExtractor);
    }
    public ClassifierOutOfTimeCrossValidator labelConverter(LabelConverter<AttributesMap> labelConverter) {
        super.labelConverter = Optional.of(labelConverter);
        return this;
    }
}
