package quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies;

import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public interface AttributeIgnoringStrategy {

    /**
     * Should this attribute be ignored
     * @param attribute
     * @param parent
     * @return
     */
    boolean ignoreAttribute(String attribute, Branch parent);

    /**
     * @return a copy of this AttributeIgnoringStrategy
     */
    AttributeIgnoringStrategy copy();
}
