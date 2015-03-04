package quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies;

import quickml.supervised.classifier.decisionTree.tree.Branch;

import java.util.Random;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public class IgnoreAttributesWithConstantProbability implements AttributeIgnoringStrategy {

    private final double ignoreAttributeProbability;
    private Random random = new Random();

    public IgnoreAttributesWithConstantProbability(double ignoreAttributeProbability) {
        this.ignoreAttributeProbability = ignoreAttributeProbability;

    }

    @Override
    public IgnoreAttributesWithConstantProbability copyThatPreservesAllFieldsThatAreNotRandomlySetByTheConstructor(){
        return new IgnoreAttributesWithConstantProbability(ignoreAttributeProbability);
    }

    @Override
    public boolean ignoreAttribute(String attribute, Branch parent) {
        if (random.nextDouble() < ignoreAttributeProbability) {
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        return "ignoreAttributeProbability = " + ignoreAttributeProbability;
    }
}
