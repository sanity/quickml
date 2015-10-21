package quickml.supervised.featureEngineering;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.data.AttributesMap;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.featureEngineering1.AttributesEnricher;
import quickml.supervised.featureEngineering1.enrichStrategies.probabilityInjector.ProbabilityEnrichStrategy;

import java.util.List;

public class ProbabilityEnrichStrategyTest {

    @Test
    public void testCreateAttributesEnricher() throws Exception {
        List<InstanceWithAttributesMap<?>> trainingData = Lists.newLinkedList();
        AttributesMap  attributes = AttributesMap.newHashMap() ;
        attributes.put("k1",2);
        attributes.put("k2",1);
        trainingData.add(new InstanceWithAttributesMap(attributes, "true"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("k1",1);
        attributes.put("k2",2);
        trainingData.add(new InstanceWithAttributesMap(attributes, "true"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("k1",2);
        attributes.put("k2",2);
        trainingData.add(new InstanceWithAttributesMap(attributes, "false"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("k1",1);
        attributes.put("k2",2);
        trainingData.add(new InstanceWithAttributesMap(attributes, "false"));
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