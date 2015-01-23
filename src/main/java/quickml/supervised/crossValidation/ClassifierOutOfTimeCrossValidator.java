package quickml.supervised.crossValidation;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 8/22/14.
 *
 */
//TODO[mk] this should be removed
public class ClassifierOutOfTimeCrossValidator extends OutOfTimeCrossValidator<AttributesMap, Serializable, PredictionMap> {
    public ClassifierOutOfTimeCrossValidator(CrossValLossFunction<Serializable, PredictionMap> crossValLossFunction, double fractionOfDataForCrossValidation, int validationTimeSliceHours, DateTimeExtractor dateTimeExtractor) {
        super(crossValLossFunction, fractionOfDataForCrossValidation, validationTimeSliceHours, dateTimeExtractor);
    }
}
