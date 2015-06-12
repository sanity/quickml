package quickml.supervised.tree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.tree.decisionTree.DTLeaf;

import java.util.Set;

/**
 * Created by alexanderhawk on 6/11/15.
 */
public class DTNumBranch extends NumBranch<ClassificationCounter>  implements DTNode{
    DTNode trueChild, falseChild;

    //TODO both getLeaf and getProb... are duplicated from DTBranch and DTCatBranch.  When java 8 is brought in, use it here.
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
    public DTNumBranch(Branch<ClassificationCounter> parent, String attribute, double probabilityOfTrueChild, double score, ClassificationCounter termStatistics, double threshold) {
        super(parent, attribute, probabilityOfTrueChild, score, termStatistics, threshold);

    }
}
