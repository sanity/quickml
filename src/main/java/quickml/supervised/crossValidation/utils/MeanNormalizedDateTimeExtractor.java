package quickml.supervised.crossValidation.utils;


import org.joda.time.DateTime;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.Utils;
import java.util.Map;


public class MeanNormalizedDateTimeExtractor<T extends InstanceWithAttributesMap> implements DateTimeExtractor<T> {

    private  Map<String, Utils.MeanStdMaxMin> meanStdMaxMinMap;

    public MeanNormalizedDateTimeExtractor(Map<String, Utils.MeanStdMaxMin> meanStdMaxMinMap) {
        this.meanStdMaxMinMap = meanStdMaxMinMap;
    }

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
        int normalizedVal = instance.getAttributes().containsKey(attrName) ?
                ((Number) instance.getAttributes().get(attrName)).intValue() : 1;
        double mean = meanStdMaxMinMap.get(attrName).getMean();
        double std = meanStdMaxMinMap.get(attrName).getNonZeroStd();
        return (int)(normalizedVal*std + mean);
    }
}
