package quickml.supervised.classifier;

import junit.framework.TestCase;
import org.apache.commons.lang.mutable.MutableInt;
import org.junit.Before;
import org.junit.Test;
import org.testng.Assert;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ClassificationPropertiesTest extends TestCase {
    List<ClassifierInstance> binaryInstances;
    List<ClassifierInstance> nonBinaryInstances;

    @Before
    private void setup(){
        binaryInstances = Arrays.asList(
                new ClassifierInstance(AttributesMap.newHashMap(), 1.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 1.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 1.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 0.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 0.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 0.0)

                );

        nonBinaryInstances = Arrays.asList(
                new ClassifierInstance(AttributesMap.newHashMap(), 2.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 2.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 2.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 1.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 1.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 1.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 0.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 0.0),
                new ClassifierInstance(AttributesMap.newHashMap(), 0.0)

                );


    }
    @Test
    public void testBinaryClassificationProperties() throws Exception {
        setup();
        BinaryClassificationProperties binaryClassificationProperties = (BinaryClassificationProperties)ClassificationProperties.getClassificationProperties(binaryInstances);
        org.junit.Assert.assertTrue(binaryClassificationProperties.classificationsAreBinary());


        org.junit.Assert.assertEquals((int)binaryClassificationProperties.majorityToMinorityRatio, 1);
        org.junit.Assert.assertTrue(binaryClassificationProperties.classificationsAreBinary());
        org.junit.Assert.assertTrue(binaryClassificationProperties.majorityClassification!=binaryClassificationProperties.minorityClassification);

    }
    @Test
    public void testGetClassificationsAndCounts() throws Exception {
        setup();
        ClassificationProperties cp = ClassificationProperties.getClassificationProperties(nonBinaryInstances);
        HashMap<Serializable, MutableInt> classificationsAndCounts = cp.getClassificationsAndCounts();
        org.junit.Assert.assertEquals(classificationsAndCounts.size(), 3);
        org.junit.Assert.assertEquals(classificationsAndCounts.get(2.0), new MutableInt(3));
        org.junit.Assert.assertEquals(classificationsAndCounts.get(1.0), new MutableInt(3));
    }
}