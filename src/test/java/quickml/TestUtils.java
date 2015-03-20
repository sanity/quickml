package quickml;

import quickml.data.AttributesMap;
import quickml.data.InstanceWithAttributesMap;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasEntry;

public class TestUtils {


    public static InstanceWithAttributesMap createClassifierInstance(final int day) {
        return new InstanceWithAttributesMap(createAttributes(day), 1.0D, 0.5);
    }

    private static AttributesMap createAttributes(final double day) {
        AttributesMap attrs = AttributesMap.newHashMap();
        attrs.put("timeOfArrival-year", 2015d);
        attrs.put("timeOfArrival-monthOfYear", 1d);
        attrs.put("timeOfArrival-dayOfMonth", day);
        attrs.put("timeOfArrival-hourOfDay", 1d);
        attrs.put("timeOfArrival-minuteOfHour", 1d);
        return attrs;
    }


}
