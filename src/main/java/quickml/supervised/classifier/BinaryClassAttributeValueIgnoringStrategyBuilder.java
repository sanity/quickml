package quickml.supervised.classifier;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;

public class BinaryClassAttributeValueIgnoringStrategyBuilder<T extends InstanceWithAttributesMap> implements AttributeValueIgnoringStrategyBuilder<T, ClassificationCounter, BinaryClassAttributeValueIgnoringStrategy<T>> {
    private BinaryClassifierDataProperties<T> bcp;
    private int minOccurancesOfAttributeValue;

    public BinaryClassAttributeValueIgnoringStrategyBuilder<T> setBcp(BinaryClassifierDataProperties<T> bcp) {
        this.bcp = bcp;
        return this;
    }

    public BinaryClassAttributeValueIgnoringStrategyBuilder<T> setMinOccurancesOfAttributeValue(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
        return this;
    }

    @Override
    public BinaryClassAttributeValueIgnoringStrategy<T> createAttributeValueIgnoringStrategy() {
        return new BinaryClassAttributeValueIgnoringStrategy<T>(bcp, minOccurancesOfAttributeValue);
    }

    public BinaryClassAttributeValueIgnoringStrategyBuilder<T> copy() {
        return new BinaryClassAttributeValueIgnoringStrategyBuilder<T>().setBcp(bcp.copy()).setMinOccurancesOfAttributeValue(minOccurancesOfAttributeValue);
    }
}