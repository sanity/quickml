package quickml.supervised.featureEngineering.enrichStrategies.probabilityInjector;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.data.*;
import quickml.supervised.featureEngineering.AttributesEnricher;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProbabilityEnrichStrategyTest {

    @Test
    public void testCreateAttributesEnricher() throws Exception {
        List<Instance<AttributesMap>> trainingData = Lists.newLinkedList();
        Map<String,Serializable> attributes = new HashMap<>();
        attributes.put("k1",2);
        attributes.put("k2",1);
        trainingData.add(new InstanceImpl(attributes, "true"));
        attributes = new HashMap<>();
        attributes.put("k1",1);
        attributes.put("k2",2);
        trainingData.add(new InstanceImpl(attributes, "true"));
        attributes = new HashMap<>();
        attributes.put("k1",2);
        attributes.put("k2",2);
        trainingData.add(new InstanceImpl(attributes, "false"));
        attributes = new HashMap<>();
        attributes.put("k1",1);
        attributes.put("k2",2);
        trainingData.add(new InstanceImpl(attributes, "false"));
        ProbabilityEnrichStrategy probabilityEnrichStrategy = new ProbabilityEnrichStrategy(Sets.newHashSet("k1", "k2"), "true");
        final AttributesEnricher attributesEnricher = probabilityEnrichStrategy.build(trainingData);
        {
            AttributesMap inputAttributes = new HashMap<>();
            inputAttributes.put("k1", 1);
            inputAttributes.put("k2", 1);
            final AttributesMap outputAttributes = attributesEnricher.apply(inputAttributes);
            Assert.assertEquals(outputAttributes.get("k1-PROB"), 0.5);
            Assert.assertEquals(outputAttributes.get("k2-PROB"), 1.0);
        }
        {
            AttributesMap inputAttributes = new HashMap<>();
            inputAttributes.put("k1", 2);
            inputAttributes.put("k2", 2);
            final AttributesMap outputAttributes = attributesEnricher.apply(inputAttributes);
            Assert.assertEquals(outputAttributes.get("k1-PROB"), 0.5);
            Assert.assertEquals(outputAttributes.get("k2-PROB"), 1.0/3.0);
        }
    }
}