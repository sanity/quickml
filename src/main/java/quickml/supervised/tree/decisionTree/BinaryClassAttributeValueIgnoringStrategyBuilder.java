package quickml.supervised.tree.decisionTree;


import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;

public class BinaryClassAttributeValueIgnoringStrategyBuilder implements AttributeValueIgnoringStrategyBuilder<ClassificationCounter, BinaryClassifierDataProperties> {
    private BinaryClassifierDataProperties bcp;

    @Override
    public AttributeValueIgnoringStrategy<ClassificationCounter> createAttributeValueIgnoringStrategy(BinaryClassifierDataProperties dataProperties) {
        return new BinaryClassAttributeValueIgnoringStrategy(bcp, minOccurancesOfAttributeValue);
    }

    private int minOccurancesOfAttributeValue;

    public BinaryClassAttributeValueIgnoringStrategyBuilder setBcp(BinaryClassifierDataProperties bcp) {
        this.bcp = bcp;
        return this;
    }

    public BinaryClassAttributeValueIgnoringStrategyBuilder setMinOccurancesOfAttributeValue(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
        return this;
    }

    public BinaryClassAttributeValueIgnoringStrategyBuilder copy() {
        return new BinaryClassAttributeValueIgnoringStrategyBuilder().setBcp(bcp.copy()).setMinOccurancesOfAttributeValue(minOccurancesOfAttributeValue);
    }
}