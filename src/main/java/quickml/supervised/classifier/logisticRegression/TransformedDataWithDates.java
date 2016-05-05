package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public interface TransformedDataWithDates<I extends Instance, D extends TransformedDataWithDates<I, D>> extends TransformedData<I, D> {

     DateTimeExtractor<I> getDateTimeExtractor();
}
