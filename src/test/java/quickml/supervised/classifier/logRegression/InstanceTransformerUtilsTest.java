package quickml.supervised.classifier.logRegression;

import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by chrisreeves on 10/14/15.
 */
public class InstanceTransformerUtilsTest {

    @Test
    public void testGetAttributeProductCounts() throws Exception {
        Map<String, Integer> map = null;//InstanceTransformerUtils.addProductAttributes(getInstances());
        assertEquals(2, (int) map.get("feature1-feature2"));
        assertEquals(1, (int) map.get("feature1-feature3"));
        assertEquals(1, (int) map.get("feature1-feature4"));
        assertEquals(2, (int) map.get("feature2-feature3"));
        assertEquals(String.valueOf(map), 4, map.size());
    }

    @Test
    public void testPopulateNameToIndexMap() throws Exception {
        HashMap<String, Integer> map = InstanceTransformerUtils.populateNameToIndexMap(getInstances());
        //order isn't deterministic, verify map contains every feature and index
        assertTrue(map.containsKey("feature1"));
        assertTrue(map.containsKey("feature2"));
        assertTrue(map.containsKey("feature3"));
        assertTrue(map.containsKey("feature4"));
        assertTrue(map.containsKey("feature5"));
        assertTrue(map.containsValue(0));
        assertTrue(map.containsValue(1));
        assertTrue(map.containsValue(2));
        assertTrue(map.containsValue(3));
        assertTrue(map.containsValue(4));
    }

    @Test
    public void testGetNumericClassLabels() throws Exception {
        Map<Serializable, Double> labels = InstanceTransformerUtils.determineNumericClassLabels(getInstances());
        assertTrue(labels.size() == 2);
        assertTrue(labels.containsKey(1.0));
        assertTrue(labels.containsKey(0.0));
    }

    private List<ClassifierInstance> getInstances() {
        List<ClassifierInstance> instances = new ArrayList<>();
        AttributesMap attributesMap = new AttributesMap();
        attributesMap.put("feature1", 1.0);
        attributesMap.put("feature2", 10.0);

        instances.add(new ClassifierInstance(attributesMap, 1.0, 1.0));

        attributesMap = new AttributesMap();
        attributesMap.put("feature1", 1.0);
        attributesMap.put("feature2", 10.0);
        attributesMap.put("feature3", 1.0);
        instances.add(new ClassifierInstance(attributesMap, 1.0, 1.0));

        attributesMap = new AttributesMap();
        attributesMap.put("feature2", 4.0);
        attributesMap.put("feature3", 1.0);
        instances.add(new ClassifierInstance(attributesMap, 0.0, 1.0));

        attributesMap = new AttributesMap();
        attributesMap.put("feature1", 1.0);
        attributesMap.put("feature4", 1.0);
        attributesMap.put("feature5", 0.0);
        instances.add(new ClassifierInstance(attributesMap, 0.0, 1.0));

        return instances;
    }

    @Test
    public void testGetAttributeProductCounts1() throws Exception {

    }
}