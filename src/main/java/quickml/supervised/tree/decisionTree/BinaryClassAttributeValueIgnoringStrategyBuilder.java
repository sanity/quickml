package quickml.supervised.tree.decisionTree;

import quickml.supervised.tree.decisionTree.tree.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.TermStatsAndOperations;

public class BinaryClassAttributeValueIgnoringStrategyBuilder<TS extends TermStatsAndOperations<TS>> implements AttributeValueIgnoringStrategyBuilder<TS, BinaryClassifierDataProperties> {
    private BinaryClassifierDataProperties bcp;

    @Override
    public AttributeValueIgnoringStrategy<TS> createAttributeValueIgnoringStrategy(BinaryClassifierDataProperties dataProperties) {
        return new BinaryClassAttributeValueIgnoringStrategy(bcp, minOccurancesOfAttributeValue);
    }

    private int minOccurancesOfAttributeValue;

    public BinaryClassAttributeValueIgnoringStrategyBuilder<TS> setBcp(BinaryClassifierDataProperties bcp) {
        this.bcp = bcp;
        return this;
    }

    public BinaryClassAttributeValueIgnoringStrategyBuilder<TS> setMinOccurancesOfAttributeValue(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
        return this;
    }

    public BinaryClassAttributeValueIgnoringStrategyBuilder<TS> copy() {
        return new BinaryClassAttributeValueIgnoringStrategyBuilder<TS>().setBcp(bcp.copy()).setMinOccurancesOfAttributeValue(minOccurancesOfAttributeValue);
    }
}