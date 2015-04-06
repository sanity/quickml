package quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies;

import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public class IgnoreAttributesWithConstantProbability implements AttributeIgnoringStrategy {

    private final double ignoreAttributeProbability;
    private ThreadLocalRandom random = ThreadLocalRandom.current();

    public IgnoreAttributesWithConstantProbability(double ignoreAttributeProbability) {
        this.ignoreAttributeProbability = ignoreAttributeProbability;
    }

    @Override
    public IgnoreAttributesWithConstantProbability copy(){
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
