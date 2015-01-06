package quickml.supervised.crossValidation.dateTimeExtractors;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public class MapDateTimeExtractor implements DateTimeExtractor<AttributesMap> {
    @Override
    public  DateTime extractDateTime(Instance<AttributesMap> instance){
        AttributesMap attributes = instance.getAttributes();
        int year = Math.max(1, (Integer)attributes.get("timeOfArrival-year"));
        int month = Math.max(1, (Integer)attributes.get("timeOfArrival-monthOfYear"));
        int day = Math.max(1, (Integer)attributes.get("timeOfArrival-dayOfMonth"));
        int hour = Math.max(1, (Integer)attributes.get("timeOfArrival-hourOfDay"));
        return new DateTime(year,month, day, hour, 1, 1, 1, DateTimeZone.UTC);
    }
}
