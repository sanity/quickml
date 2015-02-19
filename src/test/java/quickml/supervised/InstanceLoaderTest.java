package quickml.supervised;

import org.junit.Assert;
import org.junit.Test;
import quickml.data.ClassifierInstance;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by alexanderhawk on 1/5/15.
 */
public class InstanceLoaderTest {

    @Test
    public void getAdvertisingInstancesTest() {
        List<ClassifierInstance> instances = InstanceLoader.getAdvertisingInstances();
        assertEquals(instances.size(), 12000);
        ClassifierInstance lastInstance = instances.get(11999);
        Assert.assertTrue(lastInstance.getLabel().equals(0.0));
        Assert.assertTrue(lastInstance.getAttributes().get("country").equals("US"));
    }
}
