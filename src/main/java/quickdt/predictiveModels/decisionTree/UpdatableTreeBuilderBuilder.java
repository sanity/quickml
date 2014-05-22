package quickdt.predictiveModels.decisionTree;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilder;

import java.util.Map;

public class UpdatableTreeBuilderBuilder extends UpdatablePredictiveModelBuilderBuilder implements PredictiveModelBuilderBuilder<Tree, UpdatableTreeBuilder> {
    private final TreeBuilderBuilder treeBuilderBuilder;

    public UpdatableTreeBuilderBuilder() {
        this(new TreeBuilderBuilder());
    }

    public UpdatableTreeBuilderBuilder(TreeBuilderBuilder treeBuilderBuilder) {
        this.treeBuilderBuilder = treeBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        return addUpdatableParamters(treeBuilderBuilder.createDefaultParametersToOptimize());
    }

    @Override
    public UpdatableTreeBuilder buildBuilder(final Map<String, Object> cfg) throws NullPointerException {
        final TreeBuilder builder = treeBuilderBuilder.buildBuilder(cfg);
        UpdatableTreeBuilder updatableTreeBuilder = new UpdatableTreeBuilder(builder, null);
        applyUpdatableConfig(updatableTreeBuilder, cfg);
        return updatableTreeBuilder;
    }
}
