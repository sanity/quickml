package quickml.supervised.dataProcessing.instanceTranformer;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.ClassifierInstanceFactory;
import quickml.supervised.dataProcessing.AttributeCharacteristics;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import static quickml.supervised.dataProcessing.instanceTranformer.OneHotEncoder.*;

import static org.junit.Assert.*;

/**
 * Created by alexanderhawk on 10/27/15.
 */
public class OneHotEncoderTest {


    @Test
    public void transformAllTest(){
        List<ClassifierInstance> instances = getInstances();
        Map<String, AttributeCharacteristics> attributeCharacteristics = getAttributeCharacteristics();
        int minObservationsOfAnAtribute = 2;
        OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance> oneHotEncoder = new OneHotEncoder<Serializable, ClassifierInstance, ClassifierInstance>(attributeCharacteristics, new ClassifierInstanceFactory(), minObservationsOfAnAtribute);
        List<ClassifierInstance> tranformed = oneHotEncoder.transformAll(instances);
        AttributesMap attributesOfFirstInstance = tranformed.get(0).getAttributes();
        Assert.assertTrue(attributesOfFirstInstance.containsKey("b--x"));
        Assert.assertTrue(attributesOfFirstInstance.get("b--x").equals(1.0));

        Assert.assertTrue(!attributesOfFirstInstance.containsKey("c--x"));
        String insufficientDataAttribute = "c" + "--" + INSUFFICIENT_CAT_ATTR;
        Assert.assertTrue(attributesOfFirstInstance.containsKey(insufficientDataAttribute));
        Assert.assertTrue(attributesOfFirstInstance.get(insufficientDataAttribute).equals(1.0));

        Assert.assertTrue(attributesOfFirstInstance.containsKey("a"));
        Assert.assertTrue(attributesOfFirstInstance.get("a").equals(1.0));



    }

    private List<ClassifierInstance> getInstances(){
        List<ClassifierInstance> instances = Lists.newArrayList();
        AttributesMap attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", 1.0);
        attributesMap.put("b", "x");
        attributesMap.put("c", "x");

        instances.add(new ClassifierInstance(attributesMap, 1.0));

        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("a", 2.0);
        attributesMap.put("b", "x");
        attributesMap.put("c", "y");

        instances.add(new ClassifierInstance(attributesMap, 1.0));

        return instances;
    }

    private Map<String, AttributeCharacteristics> getAttributeCharacteristics(){
        Map<String, AttributeCharacteristics> attributeCharacteristics = Maps.newHashMap();
        AttributeCharacteristics a = new AttributeCharacteristics();
        a.isBoolean = false;
        a.isNumber = true;
        attributeCharacteristics.put("a", a);

        AttributeCharacteristics b = new AttributeCharacteristics();
        b.isBoolean = false;
        b.isNumber = false;
        attributeCharacteristics.put("b", b);

        AttributeCharacteristics c = new AttributeCharacteristics();
        c.isBoolean = false;
        c.isNumber = false;
        attributeCharacteristics.put("c", c);
        return attributeCharacteristics;
    }

}