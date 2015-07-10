package quickml.supervised.tree.attributeIgnoringStrategies;

import quickml.supervised.tree.nodes.Branch;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public class IgnoreAttributesWithConstantProbability implements AttributeIgnoringStrategy {
    private static final long serialVersionUID = 0L;

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
