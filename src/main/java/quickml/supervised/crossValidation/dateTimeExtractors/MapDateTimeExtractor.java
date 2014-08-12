package quickml.supervised.crossValidation.dateTimeExtractors;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public class MapDateTimeExtractor implements DateTimeExtractor<Map<String, Serializable>> {
    @Override
    public  DateTime extractDateTime(Instance<Map<String, Serializable>> instance){
        Map<String, Serializable> attributes = instance.getRegressors();
        int year = (Integer)attributes.get("timeOfArrival-year");
        int month = (Integer)attributes.get("timeOfArrival-monthOfYear");
        int day = (Integer)attributes.get("timeOfArrival-dayOfMonth");
        int hour = (Integer)attributes.get("timeOfArrival-hourOfDay");
        return new DateTime(year,month, day, hour, 0, 0, 0, DateTimeZone.UTC);
    }
}
