package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 7/1/14.
 */
public class OldAttributeValueWithClassificationCounter {
    public Serializable attributeValue;
    public OldClassificationCounter classificationCounter;
    public OldAttributeValueWithClassificationCounter(Serializable attributeValue, OldClassificationCounter classificationCounter) {
        this.attributeValue = attributeValue;
        this.classificationCounter = classificationCounter;
    }
}
