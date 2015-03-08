package quickml.supervised.classifier.decisionTree.attributeIgnoringStrategies;

import com.google.common.collect.Sets;
import org.junit.Test;
import org.testng.Assert;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.CompositeAttributeIgnoringStrategy;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesInSet;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public class AttributeIgnoringStrategiesTests {

    @Test
    public void IgnoreAttributesWithConstantProbabilityTest () {

        int ignoreAttributeProbability = 0;
        IgnoreAttributesWithConstantProbability ignoreAttributesWithConstantProbability = new IgnoreAttributesWithConstantProbability(ignoreAttributeProbability);
        int numIgnored = getNumIgnored(ignoreAttributesWithConstantProbability);
        Assert.assertEquals(numIgnored, 0);

        double ignoreAttributeProbability1 = 1.0;
        ignoreAttributesWithConstantProbability = new IgnoreAttributesWithConstantProbability(ignoreAttributeProbability1);
        numIgnored = getNumIgnored(ignoreAttributesWithConstantProbability);
        Assert.assertEquals(numIgnored, 4);
    }

    @Test
    public void IgnoreAttributesInSetTest(){
        Set<String> attributesToIgnore = Sets.newHashSet();
        attributesToIgnore.addAll(Arrays.asList("region", "hourOfDay"));
        int probabilityOfDiscardingFromAttributesToIgnore = 0;
        IgnoreAttributesInSet ignoreAttributesInSet = new IgnoreAttributesInSet(attributesToIgnore, probabilityOfDiscardingFromAttributesToIgnore);

        int numIgnored = getNumIgnored(ignoreAttributesInSet);
        Assert.assertEquals(numIgnored, 2);

        double probabilityOfDiscardingFromAttributesToIgnore1 = 1.0;
        ignoreAttributesInSet = new IgnoreAttributesInSet(attributesToIgnore, probabilityOfDiscardingFromAttributesToIgnore1);
        numIgnored = getNumIgnored(ignoreAttributesInSet);
        Assert.assertEquals(numIgnored, 0);
    }

    @Test
    public void CompositeAttributeIgnoringStrategyTest() {
        int ignoreAttributeProbability = 0;
        IgnoreAttributesWithConstantProbability ignoreAttributesWithConstantProbability = new IgnoreAttributesWithConstantProbability(ignoreAttributeProbability);

        Set<String> attributesToIgnore = Sets.newHashSet();
        attributesToIgnore.addAll(Arrays.asList("region", "hourOfDay"));
        int probabilityOfDiscardingFromAttributesToIgnore = 0;//0 ensures both region and hourOfDay will always be ignored
        IgnoreAttributesInSet ignoreAttributesInSet = new IgnoreAttributesInSet(attributesToIgnore, probabilityOfDiscardingFromAttributesToIgnore);

        CompositeAttributeIgnoringStrategy compositeAttributeIgnoringStrategy = new CompositeAttributeIgnoringStrategy(Arrays.asList(ignoreAttributesWithConstantProbability, ignoreAttributesInSet));

        int numIgnored = getNumIgnored(compositeAttributeIgnoringStrategy);
        Assert.assertEquals(numIgnored, 2);

        double ignoreAttributeProbability1 = 1.0;
        ignoreAttributesWithConstantProbability = new IgnoreAttributesWithConstantProbability(ignoreAttributeProbability1);
        compositeAttributeIgnoringStrategy = new CompositeAttributeIgnoringStrategy(Arrays.asList(ignoreAttributesWithConstantProbability, ignoreAttributesInSet));
        numIgnored = getNumIgnored(compositeAttributeIgnoringStrategy);
        Assert.assertEquals(numIgnored, 4);

    }

    private int getNumIgnored(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        int numIgnored = 0;
        List<String> attributes = Arrays.asList("domain", "creativeId", "region", "hourOfDay");
        for (String attribute : attributes) {
            if (attributeIgnoringStrategy.ignoreAttribute(attribute, null)) {
                numIgnored++;
            }
        }
        return numIgnored;
    }


}
