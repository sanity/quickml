package quickml.supervised.featureEngineering.enrichStrategies.probabilityInjector;

import com.google.common.collect.Maps;
import junit.framework.Assert;
import org.testng.annotations.Test;
import quickml.data.AttributesMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ProbabilityInjectingEnricherTest {
    @Test
    public void simpleTest() {
        final Map<String, Map<Serializable, Double>> valueProbsByAttr = Maps.newHashMap();
        Map<Serializable, Double> valueProbs = Maps.newHashMap();
        valueProbs.put(5, 0.2);
        valueProbsByAttr.put("testkey", valueProbs);
        ProbabilityInjectingEnricher probabilityInjectingEnricher = new ProbabilityInjectingEnricher(valueProbsByAttr);
        AttributesMap  inputAttributes = AttributesMap.newHashMap();
        inputAttributes.put("testkey", 5);
        final AttributesMap outputAttributes = probabilityInjectingEnricher.apply(inputAttributes);
        Assert.assertEquals("The pre-existing attribute is still there", 5, outputAttributes.get("testkey"));
        Assert.assertEquals("The newly added attribute is there", 0.2, outputAttributes.get("testkey-PROB"));
    }

}