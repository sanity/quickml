package quickdt.predictiveModels.featureEngineering.enrichStrategies.attributeCombiner;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;

import java.util.List;
import java.util.Set;

public class AttributeCombiningEnricherTest {
    @Test
    public void simpleTest() {
        Set<List<String>> attributesToCombine = Sets.newHashSet();
        attributesToCombine.add(Lists.newArrayList("k1", "k2"));
        AttributeCombiningEnricher attributeCombiningEnricher = new AttributeCombiningEnricher(attributesToCombine);
        HashMapAttributes attributes = new HashMapAttributes();
        attributes.put("k1", "a");
        attributes.put("k2", "b");
        final Map<String, Serializable> enhancedAttributes = attributeCombiningEnricher.apply(attributes);
        Assert.assertEquals(enhancedAttributes.size(), 3);
        Assert.assertEquals(enhancedAttributes.get("k1-k2"), "ab");
    }

}