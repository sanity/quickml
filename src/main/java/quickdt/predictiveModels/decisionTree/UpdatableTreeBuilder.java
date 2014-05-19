package quickdt.predictiveModels.decisionTree;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;

/**
 * Created by Chris on 5/14/2014.
 */
public class UpdatableTreeBuilder extends UpdatablePredictiveModelBuilder<Tree> {
    private final TreeBuilder treeBuilder;

    public UpdatableTreeBuilder(TreeBuilder treeBuilder) {
        this(treeBuilder, null);
    }

    public UpdatableTreeBuilder(TreeBuilder treeBuilder, Tree tree) {
        super(tree);
        this.treeBuilder = treeBuilder.updatable(true);
    }

    @Override
    public Tree buildUpdatablePredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return treeBuilder.buildPredictiveModel(trainingData);
    }

    public void updatePredictiveModel(Tree tree, final Iterable<? extends AbstractInstance> newData) {
        treeBuilder.updatePredictiveModel(tree, newData);
    }

    @Override
    public void stripData(Tree predictiveModel) {
        treeBuilder.stripData(predictiveModel);
    }
}
