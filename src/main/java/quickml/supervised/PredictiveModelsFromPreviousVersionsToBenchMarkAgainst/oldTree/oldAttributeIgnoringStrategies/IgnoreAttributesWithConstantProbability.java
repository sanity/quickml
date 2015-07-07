package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.oldAttributeIgnoringStrategies;

import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldBranch;

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
    public boolean ignoreAttribute(String attribute, OldBranch parent) {
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
