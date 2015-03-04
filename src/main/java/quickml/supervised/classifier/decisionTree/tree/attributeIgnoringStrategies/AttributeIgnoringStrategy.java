package quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies;

import quickml.supervised.classifier.decisionTree.tree.Branch;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public interface AttributeIgnoringStrategy {

    boolean ignoreAttribute(String attribute, Branch parent);
    AttributeIgnoringStrategy copyThatPreservesAllFieldsThatAreNotRandomlySetByTheConstructor();
}
