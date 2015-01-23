package quickml.supervised.crossValidation;

import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;

public class OutOfTimeCrossValidatorBuilder<R, L, P> implements CrossValidatorBuilder<R, L, P>{
    private CrossValLossFunction<L, P> crossValLossFunction;
    private double fractionOfDataForCrossValidation;
    private int validationTimeSliceHours;
    private DateTimeExtractor dateTimeExtractor;

    public OutOfTimeCrossValidatorBuilder setCrossValLossFunction(CrossValLossFunction<L,P> crossValLossFunction) {
        this.crossValLossFunction = crossValLossFunction;
        return this;
    }

    public OutOfTimeCrossValidatorBuilder setFractionOfDataForCrossValidation(double fractionOfDataForCrossValidation) {
        this.fractionOfDataForCrossValidation = fractionOfDataForCrossValidation;
        return this;
    }

    public OutOfTimeCrossValidatorBuilder setValidationTimeSliceHours(int validationTimeSliceHours) {
        this.validationTimeSliceHours = validationTimeSliceHours;
        return this;
    }

    public OutOfTimeCrossValidatorBuilder setDateTimeExtractor(DateTimeExtractor dateTimeExtractor) {
        this.dateTimeExtractor = dateTimeExtractor;
        return this;
    }

    public CrossValidator<R, L, P> createCrossValidator() {
        return new OutOfTimeCrossValidator(crossValLossFunction, fractionOfDataForCrossValidation, validationTimeSliceHours, dateTimeExtractor);
    }
}