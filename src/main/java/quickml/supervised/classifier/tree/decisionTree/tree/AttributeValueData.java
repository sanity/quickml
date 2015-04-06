package quickml.supervised.classifier.tree.decisionTree.tree;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 7/1/14.
 */
public class AttributeValueData {
    public Serializable attributeValue;
    public ClassificationCounter classificationCounter;
    public AttributeValueData(Serializable attributeValue, ClassificationCounter classificationCounter) {
        this.attributeValue = attributeValue;
        this.classificationCounter = classificationCounter;
    }
}
