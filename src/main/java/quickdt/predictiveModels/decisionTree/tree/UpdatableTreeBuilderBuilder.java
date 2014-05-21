package quickdt.predictiveModels.decisionTree.tree;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilderBuilder;
import quickdt.predictiveModels.decisionTree.UpdatableTreeBuilder;

import java.util.Map;

public class UpdatableTreeBuilderBuilder implements PredictiveModelBuilderBuilder<Tree, UpdatableTreeBuilder> {
    private final Integer rebuildThreshold;
    private final Integer splitThreshold;
    private final TreeBuilderBuilder treeBuilderBuilder;

    public UpdatableTreeBuilderBuilder(TreeBuilderBuilder treeBuilderBuilder) {
        this(treeBuilderBuilder, null, null);
    }

    public UpdatableTreeBuilderBuilder(TreeBuilderBuilder treeBuilderBuilder, Integer rebuildThreshold, Integer splitThreshold) {
        this.treeBuilderBuilder = treeBuilderBuilder;
        this.rebuildThreshold = rebuildThreshold;
        this.splitThreshold = splitThreshold;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return treeBuilderBuilder.createDefaultParametersToOptimize();
    }

    @Override
    public UpdatableTreeBuilder buildBuilder(final Map<String, Object> cfg) throws NullPointerException {
        return new UpdatableTreeBuilder(treeBuilderBuilder.buildBuilder(cfg), rebuildThreshold, splitThreshold);
    }
}
