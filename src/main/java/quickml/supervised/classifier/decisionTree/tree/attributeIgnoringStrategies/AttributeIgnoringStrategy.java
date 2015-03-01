package quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies;

/**
 * Created by alexanderhawk on 2/28/15.
 */
public interface AttributeIgnoringStrategy {

    boolean ignoreAttribute(String attribute);
    AttributeIgnoringStrategy copyThatPreservesAllFieldsThatAreNotRandomlySetByTheConstructor();
}
