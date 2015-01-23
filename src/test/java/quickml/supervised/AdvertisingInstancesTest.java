package quickml.supervised;

import junit.framework.Assert;
import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 1/5/15.
 */
public class AdvertisingInstancesTest {

    @Test
    public void getAdvertisingInstancesTest() {
        List<Instance<AttributesMap, Serializable>> instances = AdvertisingInstances.getAdvertisingInstances();
        Assert.assertEquals(instances.size(), 12000);
        Instance<AttributesMap, Serializable> lastInstance = instances.get(11999);
        Assert.assertTrue(lastInstance.getLabel().equals(0.0));
        Assert.assertTrue(lastInstance.getAttributes().get("country").equals("US"));
    }
}
