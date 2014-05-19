package quickdt.crossValidation;
import org.joda.time.DateTime;
import quickdt.data.AbstractInstance;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public interface DateTimeExtractor {
       DateTime extractDateTime(AbstractInstance instance);
}
