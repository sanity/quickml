package quickml.supervised.tree.initializers;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.configurations.TreeBuildContext;
import quickml.supervised.tree.configurations.TreeConfig;
import quickml.supervised.tree.nodes.Node;

import java.util.List;

/**
 * Created by alexanderhawk on 6/19/15.
 */
public interface Initializer<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>>  {
    TreeBuildContext<L, I, VC, N> initialize(TreeConfig<L, I, VC, N> treeConfig, List<I> trainingData);
}
