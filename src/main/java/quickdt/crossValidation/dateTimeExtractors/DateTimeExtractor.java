package quickdt.crossValidation.dateTimeExtractors;
import org.joda.time.DateTime;
import quickdt.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public interface DateTimeExtractor <R> {
      DateTime extractDateTime(Instance<R> instance);
}
