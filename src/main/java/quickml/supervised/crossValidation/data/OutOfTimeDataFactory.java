package quickml.supervised.crossValidation.data;

import quickml.data.instances.Instance;
import quickml.supervised.classifier.logisticRegression.TransformedDataWithDates;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public class OutOfTimeDataFactory<R extends Instance, D extends TransformedDataWithDates<R,D>> implements TrainingDataCyclerFactory<R,D> {
    private double crossValidationFraction;
    private int timeSliceHours;

    public OutOfTimeDataFactory(double crossValidationFraction, int timeSliceHours) {
        this.crossValidationFraction = crossValidationFraction;
        this.timeSliceHours = timeSliceHours;
    }

    @Override
    public OutOfTimeData<R> getTrainingDataCycler(D data) {
        return new OutOfTimeData<>(data.getTransformedInstances(), crossValidationFraction, timeSliceHours, data.getDateTimeExtractor());
    }
}
