package quickml.supervised.tree.decisionTree.attributeValueIgnoringStrategies;


import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

public class MultiClassAttributeValueIgnoringStrategyBuilder implements AttributeValueIgnoringStrategyBuilder<ClassificationCounter> {
    private static final long serialVersionUID = 0L;

    public MultiClassAttributeValueIgnoringStrategyBuilder(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
    }

    @Override
    public AttributeValueIgnoringStrategy<ClassificationCounter> createAttributeValueIgnoringStrategy(ClassificationCounter cc) {
        return new MultiClassAtributeValueIgnoringStrategy(minOccurancesOfAttributeValue);
    }

    private int minOccurancesOfAttributeValue;


    public MultiClassAttributeValueIgnoringStrategyBuilder setMinOccurancesOfAttributeValue(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
        return this;
    }

    public MultiClassAttributeValueIgnoringStrategyBuilder copy() {
        return new MultiClassAttributeValueIgnoringStrategyBuilder(minOccurancesOfAttributeValue).setMinOccurancesOfAttributeValue(minOccurancesOfAttributeValue);
    }
}