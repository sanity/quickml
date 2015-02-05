package quickml.supervised.alternative.optimizer;


import org.joda.time.DateTime;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;

import java.io.Serializable;

public class OnespotDateTimeExtractor implements DateTimeExtractor<ClassifierInstance> {

    @Override
    public DateTime extractDateTime(ClassifierInstance instance) {
        int year = attrVal(instance, "timeOfArrival-year");
        int month = attrVal(instance,"timeOfArrival-monthOfYear");
        int day = attrVal(instance,"timeOfArrival-dayOfMonth");
        int hour = attrVal(instance, "timeOfArrival-hourOfDay");
        int minute = attrVal(instance, "timeOfArrival-minuteOfHour");
        return new DateTime(year, month, day, hour, minute, 0, 0);
    }

    private int attrVal(Instance<AttributesMap, Serializable> instance, String attrName) {
        return instance.getAttributes().containsKey(attrName) ?
                ((Number) instance.getAttributes().get(attrName)).intValue() : 1 ;
    }
}
