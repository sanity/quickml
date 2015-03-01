package quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public class CompositeAttributeIgnoringStrategy implements AttributeIgnoringStrategy {
    private List<AttributeIgnoringStrategy> attributeIgnoringStrategies = Lists.newArrayList();

    public CompositeAttributeIgnoringStrategy(List<AttributeIgnoringStrategy> attributeIgnoringStrategies) {
        this.attributeIgnoringStrategies = attributeIgnoringStrategies;
    }

    @Override
    public CompositeAttributeIgnoringStrategy copyThatPreservesAllFieldsThatAreNotRandomlySetByTheConstructor() {
        List<AttributeIgnoringStrategy> copies = Lists.newArrayList();
        for (AttributeIgnoringStrategy attributeIgnoringStrategy : attributeIgnoringStrategies) {
            copies.add(attributeIgnoringStrategy.copyThatPreservesAllFieldsThatAreNotRandomlySetByTheConstructor());
        }
        return new CompositeAttributeIgnoringStrategy(copies);
    }

    @Override
    public boolean ignoreAttribute(String attribute) {
        for (AttributeIgnoringStrategy attributeIgnoringStrategy : attributeIgnoringStrategies) {
            if (attributeIgnoringStrategy.ignoreAttribute(attribute)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "CompositeAttributeIgnoringStrategy{" +
                "attributeIgnoringStrategies=" + attributeIgnoringStrategies +
                '}';
    }
}
