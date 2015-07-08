package quickml.supervised.featureEngineering;

import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.data.AttributesMap;
import quickml.supervised.featureEngineering1.enrichStrategies.attributeCombiner.AttributeCombiningEnricher;

import java.util.List;
import java.util.Set;

public class AttributeCombiningEnricherTest {
    @Test
    public void simpleTest() {
        Set<List<String>> attributesToCombine = Sets.newHashSet();
        attributesToCombine.add(Lists.newArrayList("k1", "k2"));
        AttributeCombiningEnricher attributeCombiningEnricher = new AttributeCombiningEnricher(attributesToCombine);
        AttributesMap attributes = AttributesMap.newHashMap();
        attributes.put("k1", "a");
        attributes.put("k2", "b");
        final AttributesMap enhancedAttributes = attributeCombiningEnricher.apply(attributes);
        Assert.assertEquals(enhancedAttributes.size(), 3);
        Assert.assertEquals(enhancedAttributes.get("k1-k2"), "ab");
    }

}