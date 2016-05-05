package quickml.supervised.dataProcessing.instanceTranformer;

import com.beust.jcommander.internal.Lists;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by alexanderhawk on 10/22/15.
 */
public class CommonCoocurrenceProductFeatureAppenderTest {
    @Test
    public void addNonNumerericProductAttributesTest() {

        ProductFeatureAppender productFeatureAppender = new CommonCoocurrenceProductFeatureAppender<ClassifierInstance>()
                .setMinObservationsOfRawAttribute(2)
                .setAllowCategoricalProductFeatures(true)
                .setAllowNumericProductFeatures(false)
                .setApproximateOverlap(true)
                .setMinOverlap(2);


        List<ClassifierInstance> instances = productFeatureAppender.addProductAttributes(getInstances1());
        ClassifierInstance instance = instances.get(1);
        AttributesMap attributes =  instance.getAttributes();
        Assert.assertTrue(attributes.containsKey("1-3"));
        Assert.assertEquals((double) attributes.get("1-3"), 1.0, 1E-5);
        Assert.assertTrue(!attributes.containsKey("1-2"));
        Assert.assertTrue(!attributes.containsKey("1-4"));


    }

    @Test
    public void addNumericOnlyProductAttributesTest() {

        ProductFeatureAppender productFeatureAppender = new CommonCoocurrenceProductFeatureAppender<ClassifierInstance>().setMinObservationsOfRawAttribute(2)
                .setAllowCategoricalProductFeatures(false)
                .setAllowNumericProductFeatures(true)
                .setApproximateOverlap(true)
                .setMinOverlap(2);


        List<ClassifierInstance> instances = productFeatureAppender.addProductAttributes(getInstances1());
        ClassifierInstance instance = instances.get(1);
        AttributesMap attributes =  instance.getAttributes();
        Assert.assertTrue(attributes.containsKey("2-4"));


         instance = instances.get(1);
        Assert.assertEquals((double) instance.getAttributes().get("2-4"), 7.0, 1E-5);
        Assert.assertTrue(!attributes.containsKey("1-3"));


    }

    @Test
    public void addAllProductAttributesTest() {

        ProductFeatureAppender productFeatureAppender = new CommonCoocurrenceProductFeatureAppender<ClassifierInstance>()
                .setMinObservationsOfRawAttribute(2)
                .setAllowCategoricalProductFeatures(true)
                .setAllowNumericProductFeatures(true)
                .setApproximateOverlap(true)
                .setMinOverlap(2);


        List<ClassifierInstance> instances = productFeatureAppender.addProductAttributes(getInstances1());
        ClassifierInstance instance = instances.get(1);
        AttributesMap attributes =  instance.getAttributes();
        Assert.assertTrue(attributes.containsKey("2-4"));
        Assert.assertTrue(attributes.containsKey("2-3"));
        instance = instances.get(1);
        Assert.assertEquals((Double) instance.getAttributes().get("2-4"), 7.0, 1E-5);


    }


    private List<ClassifierInstance> getInstances1() {
        List<ClassifierInstance> instances = Lists.newArrayList();
        AttributesMap attributesMap1 = AttributesMap.newHashMap();
        AttributesMap attributesMap2 = AttributesMap.newHashMap();
        AttributesMap attributesMap3 = AttributesMap.newHashMap();
        attributesMap1.put("1", 0.0);//what happens an attribute with 0 val is present in sparse classifier instance?
        attributesMap1.put("2", 9.0);
        attributesMap1.put("3", 1.0);
        attributesMap1.put("4", 5.0);

        attributesMap2.put("1", 1.0);
        attributesMap2.put("2", 7.0);
        attributesMap2.put("3", 1.0);
        attributesMap2.put("4", 1.0);

        attributesMap3.put("1", 1.0);
        attributesMap3.put("2", 7.0);
        attributesMap3.put("3", 1.0);
        attributesMap3.put("4", 1.0);

        instances.add(new ClassifierInstance(attributesMap1, 1.0));
        instances.add(new ClassifierInstance(attributesMap2, 0.0));
        instances.add(new ClassifierInstance(attributesMap3, 0.0));
        return instances;

    }
}