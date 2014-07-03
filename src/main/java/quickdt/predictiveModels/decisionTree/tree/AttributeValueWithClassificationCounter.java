package quickdt.predictiveModels.decisionTree.tree;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 7/1/14.
 */
public class AttributeValueWithClassificationCounter {
    public Serializable attributeValue;
    public ClassificationCounter classificationCounter;
    public AttributeValueWithClassificationCounter(Serializable attributeValue, ClassificationCounter classificationCounter) {
        this.attributeValue = attributeValue;
        this.classificationCounter = classificationCounter;
    }
}
