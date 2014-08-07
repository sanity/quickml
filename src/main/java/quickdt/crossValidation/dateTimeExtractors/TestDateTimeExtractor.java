package quickdt.crossValidation.dateTimeExtractors;

import org.joda.time.DateTime;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public class TestDateTimeExtractor implements DateTimeExtractor {
    @Override
    public DateTime extractDateTime(AbstractInstance instance){
        Map<String, Serializable> attributes = instance.getRegressors();
        int currentTimeMillis = (Integer)attributes.get("currentTimeMillis");
        return new DateTime(currentTimeMillis);
    }
}
