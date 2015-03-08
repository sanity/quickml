package quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies;

import quickml.supervised.classifier.decisionTree.tree.Branch;

/**
 * Created by alexanderhawk on 3/2/15.
 */
public class AttributeNameAndParent {
    public final String attribute;
    public final Branch branch;

    public AttributeNameAndParent(String attribute, Branch branch) {
        this.attribute = attribute;
        this.branch = branch;
    }

}
