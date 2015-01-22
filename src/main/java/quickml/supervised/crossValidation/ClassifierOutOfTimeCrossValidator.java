package quickml.supervised.crossValidation;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class ClassifierOutOfTimeCrossValidator extends OutOfTimeCrossValidator<AttributesMap, PredictionMap> {
    public ClassifierOutOfTimeCrossValidator(CrossValLossFunction<PredictionMap> crossValLossFunction, double fractionOfDataForCrossValidation, int validationTimeSliceHours, DateTimeExtractor dateTimeExtractor) {
        super(crossValLossFunction, fractionOfDataForCrossValidation, validationTimeSliceHours, dateTimeExtractor);
    }
}
