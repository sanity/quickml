package quickml.data;


import org.joda.time.DateTime;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;


public class OnespotDateTimeExtractor<T extends ClassifierInstance> implements DateTimeExtractor<T> {

    @Override
    public DateTime extractDateTime(T instance) {
        int year = attrVal(instance, "timeOfArrival-year");
        int month = attrVal(instance,"timeOfArrival-monthOfYear");
        int day = attrVal(instance,"timeOfArrival-dayOfMonth");
        int hour = attrVal(instance, "timeOfArrival-hourOfDay");
        int minute = attrVal(instance, "timeOfArrival-minuteOfHour");
        return new DateTime(year, month, day, hour, minute, 0, 0);
    }

    private int attrVal(T instance, String attrName) {
        return instance.getAttributes().containsKey(attrName) ?
                ((Number) instance.getAttributes().get(attrName)).intValue() : 1 ;
    }
}
