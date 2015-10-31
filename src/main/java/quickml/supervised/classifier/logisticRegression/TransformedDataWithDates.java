package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public abstract class TransformedDataWithDates<I extends Instance, D extends TransformedDataWithDates<I, D>> extends TransformedData<I, D>  {
    public TransformedDataWithDates() {
        super();
    }

    public abstract DateTimeExtractor<I> getDateTimeExtractor();
}
