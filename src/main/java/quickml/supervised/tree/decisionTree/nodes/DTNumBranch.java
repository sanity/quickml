package quickml.supervised.tree.decisionTree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.NumBranch;

import java.util.Set;

/**
 * Created by alexanderhawk on 6/11/15.
 */
public class DTNumBranch extends NumBranch<ClassificationCounter, DTNode> implements DTNode{

    @Override
    public DTLeaf getLeaf(final AttributesMap attributes) {
        if (decide(attributes))
            return trueChild.getLeaf(attributes);
        else
            return falseChild.getLeaf(attributes);
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
    public DTNumBranch(Branch<ClassificationCounter, DTNode> parent, String attribute, double probabilityOfTrueChild, double score, ClassificationCounter termStatistics, double threshold) {
        super(parent, attribute, probabilityOfTrueChild, score, termStatistics, threshold);

    }
}
