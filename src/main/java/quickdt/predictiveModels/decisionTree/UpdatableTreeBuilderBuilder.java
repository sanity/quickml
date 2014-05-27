package quickdt.predictiveModels.decisionTree;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilderUtils;
import quickdt.predictiveModels.WrappedUpdatablePredictiveModelBuilder;

import java.util.Map;

/**
 * Created by Chris on 5/22/2014.
 */
public class UpdatableTreeBuilderBuilder implements PredictiveModelBuilderBuilder {
    private final TreeBuilderBuilder treeBuilderBuilder;

    public UpdatableTreeBuilderBuilder(TreeBuilderBuilder treeBuilderBuilder) {
        this.treeBuilderBuilder = treeBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> map = treeBuilderBuilder.createDefaultParametersToOptimize();
        UpdatablePredictiveModelBuilderBuilderUtils.addUpdatableParamters(map);
        return map;
    }

    @Override
    public WrappedUpdatablePredictiveModelBuilder buildBuilder(Map predictiveModelConfig) {
        TreeBuilder treeBuilder = treeBuilderBuilder.buildBuilder(predictiveModelConfig);
        WrappedUpdatablePredictiveModelBuilder wrappedBuilder = new WrappedUpdatablePredictiveModelBuilder(treeBuilder);
        UpdatablePredictiveModelBuilderBuilderUtils.applyUpdatableConfig(wrappedBuilder, predictiveModelConfig);
        return wrappedBuilder;
    }
}
