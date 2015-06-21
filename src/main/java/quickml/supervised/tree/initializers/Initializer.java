package quickml.supervised.tree.initializers;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.treeBuildContexts.TreeContext;
import quickml.supervised.tree.treeBuildContexts.TreeContextBuilder;
import quickml.supervised.tree.nodes.Node;

import java.util.List;

/**
 * Created by alexanderhawk on 6/19/15.
 */
public interface Initializer<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>>  {
    TreeContext<L, I, VC, N> initialize(TreeContextBuilder<L, I, VC, N> treeContextBuilder, List<I> trainingData);
}
