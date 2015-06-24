package quickml.decisionTree.attributeValueIgnoringStrategies;


import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.decisionTree.valueCounters.ClassificationCounter;

public class MultiClassAttributeValueIgnoringStrategyBuilder implements AttributeValueIgnoringStrategyBuilder<ClassificationCounter> {

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