package quickml.supervised.classifier;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TermStatisticsOperations;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TermStatsAndOperations;

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