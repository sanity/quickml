package quickml.supervised.tree.decisionTree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;

import java.util.Set;

/**
 * Created by alexanderhawk on 4/28/15.
 */
public abstract class DTBranch extends Branch<ClassificationCounter, DTNode> implements DTNode {

    public DTBranch(Branch<ClassificationCounter, DTNode> parent, final String attribute, double probabilityOfTrueChild, double score, ClassificationCounter termStatistics) {
        super(parent, attribute, probabilityOfTrueChild, score, termStatistics);
    }

    @Override
    public double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attributesToIgnore) {
        //TODO[mk] - check with Alex
        if (attributesToIgnore.contains(this.attribute)) {
            return this.probabilityOfTrueChild * trueChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore) +
                    (1 - probabilityOfTrueChild) * falseChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
        } else {
            if (decide(attributes)) {
                return trueChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
            }
            else {
                return falseChild.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
            }
        }
    }

    @Override
    public DTLeaf getLeaf(final AttributesMap attributes) {
        if (decide(attributes))
            return trueChild.getLeaf(attributes);
        else
            return falseChild.getLeaf(attributes);
    }
}
