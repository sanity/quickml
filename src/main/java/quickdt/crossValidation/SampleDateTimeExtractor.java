package quickdt.crossValidation;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;

/**
 * Created by alexanderhawk on 5/6/14.
 */
public class SampleDateTimeExtractor implements DateTimeExtractor {
    @Override
    public DateTime extractDateTime(AbstractInstance instance){
        Attributes attributes = instance.getAttributes();
        int year = (Integer)attributes.get("timeOfArrival-year");
        int month = (Integer)attributes.get("timeOfArrival-monthOfYear");
        int day = (Integer)attributes.get("timeOfArrival-dayOfMonth");
        int hour = (Integer)attributes.get("timeOfArrival-hourOfDay");
        return new DateTime(year,month, day, hour, 0, 0, 0, DateTimeZone.UTC);
    }
}
