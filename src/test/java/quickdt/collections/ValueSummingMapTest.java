package quickdt.collections;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by ian on 3/2/14.
 */
public class ValueSummingMapTest {
    @Test
    public void simpleTest() {
        ValueSummingMap<String> valueSummingMap = new ValueSummingMap<String>();
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 0.0);
        valueSummingMap.put("a", 1);
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 1.0);
        valueSummingMap.put("a", 1);
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 1.0);
        valueSummingMap.put("b", 1);
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 2.0);
        valueSummingMap.addToValue("b", 2);
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 4.0);
        Assert.assertEquals(valueSummingMap.get("b"), 3.0);
        valueSummingMap.remove("b");
        Assert.assertEquals(valueSummingMap.getSumOfValues(), 1.0);
    }
}
