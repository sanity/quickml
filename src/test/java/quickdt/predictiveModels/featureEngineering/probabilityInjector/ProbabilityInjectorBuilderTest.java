package quickdt.predictiveModels.featureEngineering.probabilityInjector;

import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickdt.data.*;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.featureEngineering.AttributesEnricher;

import java.util.List;

public class ProbabilityInjectorBuilderTest {

    @Test
    public void testCreateAttributesEnricher() throws Exception {
        List<Instance> trainingData = Lists.newLinkedList();
        trainingData.add(Instance.create("true", "k1", 2, "k2", 1));
        trainingData.add(Instance.create("true", "k1", 1, "k2", 2));
        trainingData.add(Instance.create("false", "k1", 2, "k2", 2));
        trainingData.add(Instance.create("false", "k1", 1, "k2", 2));
        ProbabilityInjectorBuilder probabilityInjectorBuilder = new ProbabilityInjectorBuilder(new TreeBuilder(), Sets.newHashSet("k1", "k2"), "true");
        final AttributesEnricher attributesEnricher = probabilityInjectorBuilder.createAttributesEnricher(trainingData);
        {
            Attributes inputAttributes = new HashMapAttributes();
            inputAttributes.put("k1", 1);
            inputAttributes.put("k2", 1);
            final Attributes outputAttributes = attributesEnricher.apply(inputAttributes);
            Assert.assertEquals(outputAttributes.get("k1-PROB"), 0.5);
            Assert.assertEquals(outputAttributes.get("k2-PROB"), 1.0);
        }
        {
            Attributes inputAttributes = new HashMapAttributes();
            inputAttributes.put("k1", 2);
            inputAttributes.put("k2", 2);
            final Attributes outputAttributes = attributesEnricher.apply(inputAttributes);
            Assert.assertEquals(outputAttributes.get("k1-PROB"), 0.5);
            Assert.assertEquals(outputAttributes.get("k2-PROB"), 1.0/3.0);
        }
    }
}