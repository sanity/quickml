package quickml.supervised.featureEngineering.enrichStrategies.probabilityInjector;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.featureEngineering.AttributesEnricher;

import java.util.List;

public class ProbabilityEnrichStrategyTest {

    @Test
    public void testCreateAttributesEnricher() throws Exception {
        List<ClassifierInstance> trainingData = Lists.newLinkedList();
        AttributesMap  attributes = AttributesMap.newHashMap() ;
        attributes.put("k1",2);
        attributes.put("k2",1);
        trainingData.add(new ClassifierInstance(attributes, "true"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("k1",1);
        attributes.put("k2",2);
        trainingData.add(new ClassifierInstance(attributes, "true"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("k1",2);
        attributes.put("k2",2);
        trainingData.add(new ClassifierInstance(attributes, "false"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("k1",1);
        attributes.put("k2",2);
        trainingData.add(new ClassifierInstance(attributes, "false"));
        ProbabilityEnrichStrategy probabilityEnrichStrategy = new ProbabilityEnrichStrategy(Sets.newHashSet("k1", "k2"), "true");
        final AttributesEnricher attributesEnricher = probabilityEnrichStrategy.build(trainingData);
        {
            AttributesMap inputAttributes = AttributesMap.newHashMap() ;
            inputAttributes.put("k1", 1);
            inputAttributes.put("k2", 1);
            final AttributesMap outputAttributes = attributesEnricher.apply(inputAttributes);
            Assert.assertEquals(outputAttributes.get("k1-PROB"), 0.5);
            Assert.assertEquals(outputAttributes.get("k2-PROB"), 1.0);
        }
        {
            AttributesMap inputAttributes = AttributesMap.newHashMap() ;
            inputAttributes.put("k1", 2);
            inputAttributes.put("k2", 2);
            final AttributesMap outputAttributes = attributesEnricher.apply(inputAttributes);
            Assert.assertEquals(outputAttributes.get("k1-PROB"), 0.5);
            Assert.assertEquals(outputAttributes.get("k2-PROB"), 1.0/3.0);
        }
    }
}