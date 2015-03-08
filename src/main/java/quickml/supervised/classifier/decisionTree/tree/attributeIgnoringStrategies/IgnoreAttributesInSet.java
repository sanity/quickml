package quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies;

import com.google.common.collect.Sets;
import quickml.supervised.classifier.decisionTree.tree.Branch;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public class IgnoreAttributesInSet implements AttributeIgnoringStrategy {
    private final HashSet<String> attributesToIgnore = Sets.newHashSet();
    private final Set<String> proposedAttributesToIgnore;
    private final double discardProbability;
    private Random random = new Random();

    public IgnoreAttributesInSet(Set<String> attributesToIgnore, double probabilityOfDiscardingFromAttributesToIgnore) {
        this.proposedAttributesToIgnore = attributesToIgnore;
        this.discardProbability = probabilityOfDiscardingFromAttributesToIgnore;
        setAttributesToIgnore();
    }

    private void setAttributesToIgnore() {
        for (String attribute : proposedAttributesToIgnore) {
            if (random.nextDouble() > discardProbability) {
                attributesToIgnore.add(attribute);
            }
        }
    }

    @Override
    public IgnoreAttributesInSet copy(){
        return new IgnoreAttributesInSet(proposedAttributesToIgnore, discardProbability);
    }

    @Override
    public boolean ignoreAttribute(String attribute, Branch Parent) {
        if (attributesToIgnore.contains(attribute)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "IgnoreAttributesInSet{" + "proposedAttributesToIgnore=" + proposedAttributesToIgnore +
                ", discardProbability=" + discardProbability +
                '}';
    }
}
