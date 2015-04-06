package quickml.supervised.classifier.tree.decisionTree.tree.nodes;

import com.google.common.collect.ImmutableList;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.BinaryClassAttributeValueIgnoringStrategy;
import quickml.supervised.classifier.BinaryClassAttributeValueIgnoringStrategyBuilder;
import quickml.supervised.classifier.AttributeAndBinaryClassificationProperties;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.BranchType;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinderBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.TwoClassCategoricalBranchFinderForGreedyDecisionTree;
import static quickml.supervised.classifier.tree.decisionTree.tree.ForestOptions.*;

import java.util.Map;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class TwoClassCategoricalBranchFinderBuilderForGreedyDecisionTree<T extends InstanceWithAttributesMap> implements BranchFinderBuilder<T, AttributeAndBinaryClassificationProperties<T>>{

    private BinaryClassAttributeValueIgnoringStrategyBuilder<T> attributeValueIgnoringStrategyBuilder;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private Scorer<ClassificationCounter> scorer;
    private int minLeafInstances;



    public TwoClassCategoricalBranchFinderBuilderForGreedyDecisionTree<T> attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
        return this;
    }


    public TwoClassCategoricalBranchFinderBuilderForGreedyDecisionTree<T> scorer(Scorer<ClassificationCounter> scorer) {
        this.scorer = scorer;
        return this;
    }

    public TwoClassCategoricalBranchFinderBuilderForGreedyDecisionTree<T> minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }

    public TwoClassCategoricalBranchFinderBuilderForGreedyDecisionTree<T> attributeValueIgnoringStrategyBuilder(BinaryClassAttributeValueIgnoringStrategyBuilder<T> attributeValueIgnoringStrategyBuilder) {
        this.attributeValueIgnoringStrategyBuilder = attributeValueIgnoringStrategyBuilder;
        return this;
    }


    @Override
    public BranchFinderBuilder<T, AttributeAndBinaryClassificationProperties<T>> copy() {
        TwoClassCategoricalBranchFinderBuilderForGreedyDecisionTree<T> copy = new TwoClassCategoricalBranchFinderBuilderForGreedyDecisionTree<T>();
        copy.attributeIgnoringStrategy = attributeIgnoringStrategy.copy();
        copy.attributeValueIgnoringStrategyBuilder = attributeValueIgnoringStrategyBuilder.copy();
        copy.scorer = scorer;        //scorer should be set in the cofig builder if it is null here.
        return copy;

    }

    @Override
    public void update(Map<String, Object> cfg) {
        if (cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY))
            attributeIgnoringStrategy= (AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_IGNORING_STRATEGY);
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER))
            attributeValueIgnoringStrategyBuilder= (BinaryClassAttributeValueIgnoringStrategyBuilder<T>) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER);
        if (cfg.containsKey(SCORER))
            scorer = (Scorer<ClassificationCounter>) cfg.get(SCORER);
    }

    @Override
    public BranchFinder<T> buildBranchFinder(AttributeAndBinaryClassificationProperties<T> bcp) {
        BinaryClassAttributeValueIgnoringStrategy<T> binaryClassAttributeValueIgnoringStrategy = attributeValueIgnoringStrategyBuilder.setBcp(bcp).createAttributeValueIgnoringStrategy();
        ImmutableList<String> candidatetAttributes = bcp.getCandidateAttributesByBranchType().get(BranchType.CATEGORICAL);
        BranchFinder<T> branchFinder =
                new TwoClassCategoricalBranchFinderForGreedyDecisionTree<>(binaryClassAttributeValueIgnoringStrategy,attributeIgnoringStrategy, scorer, candidatetAttributes, minLeafInstances);
        return branchFinder;
    }
}
