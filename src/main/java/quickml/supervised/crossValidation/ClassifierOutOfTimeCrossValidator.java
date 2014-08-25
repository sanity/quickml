package quickml.supervised.crossValidation;

import org.joda.time.Period;
import quickml.data.MapWithDefaultOfZero;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class ClassifierOutOfTimeCrossValidator extends OutOfTimeCrossValidator<Map<String, Serializable>, MapWithDefaultOfZero> {
    public ClassifierOutOfTimeCrossValidator(CrossValLossFunction<MapWithDefaultOfZero> crossValLossFunction, double fractionOfDataForCrossValidation, int validationTimeSliceHours, DateTimeExtractor dateTimeExtractor) {
        super(crossValLossFunction, fractionOfDataForCrossValidation, validationTimeSliceHours, dateTimeExtractor);
    }
}
