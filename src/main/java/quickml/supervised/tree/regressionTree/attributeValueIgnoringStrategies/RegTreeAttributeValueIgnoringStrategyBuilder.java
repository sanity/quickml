package quickml.supervised.tree.regressionTree.attributeValueIgnoringStrategies;


import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import javax.annotation.Nullable;

public class RegTreeAttributeValueIgnoringStrategyBuilder implements AttributeValueIgnoringStrategyBuilder<MeanValueCounter> {

    private static final long serialVersionUID = 0L;
    private int minOccurancesOfAttributeValue;

    public RegTreeAttributeValueIgnoringStrategyBuilder(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
    }

    @Override
    public AttributeValueIgnoringStrategy<MeanValueCounter> createAttributeValueIgnoringStrategy(MeanValueCounter mv) {
        return new RegTreeAttributeValueIgnoringStrategy(minOccurancesOfAttributeValue);
    }



    public RegTreeAttributeValueIgnoringStrategyBuilder setMinOccurancesOfAttributeValue(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
        return this;
    }

    public RegTreeAttributeValueIgnoringStrategyBuilder copy() {
        return new RegTreeAttributeValueIgnoringStrategyBuilder(minOccurancesOfAttributeValue).setMinOccurancesOfAttributeValue(minOccurancesOfAttributeValue);
    }



}