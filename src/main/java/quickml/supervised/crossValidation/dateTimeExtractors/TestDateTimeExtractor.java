package quickml.supervised.crossValidation.dateTimeExtractors;

import org.joda.time.DateTime;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public class TestDateTimeExtractor implements DateTimeExtractor<AttributesMap> {
    @Override
    public DateTime extractDateTime(Instance<AttributesMap> instance){

        AttributesMap attributes = instance.getAttributes();
        int currentTimeMillis = (Integer)attributes.get("currentTimeMillis");
        return new DateTime(currentTimeMillis);
    }
}
