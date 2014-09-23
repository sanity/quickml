package quickml.supervised.crossValidation.dateTimeExtractors;
import org.joda.time.DateTime;
import quickml.data.Instance;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public interface DateTimeExtractor <R> {
    DateTime extractDateTime(Instance<R> instance);
}
