package quickdt.crossValidation;

import org.joda.time.DateTime;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public class TestDateTimeExtractor implements DateTimeExtractor {
    @Override
    public DateTime extractDateTime(AbstractInstance instance){
        Attributes attributes = instance.getAttributes();
        int currentTimeMillis = (Integer)attributes.get("currentTimeMillis");
        return new DateTime(currentTimeMillis);
    }
}
