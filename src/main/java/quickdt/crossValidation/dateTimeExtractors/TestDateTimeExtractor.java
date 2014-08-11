package quickdt.crossValidation.dateTimeExtractors;

import org.joda.time.DateTime;
import quickdt.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public class TestDateTimeExtractor implements DateTimeExtractor<Map<String, Serializable>> {
    @Override
    public DateTime extractDateTime(Instance<Map<String, Serializable>> instance){

        Map<String, Serializable> attributes = instance.getRegressors();
        int currentTimeMillis = (Integer)attributes.get("currentTimeMillis");
        return new DateTime(currentTimeMillis);
    }
}
