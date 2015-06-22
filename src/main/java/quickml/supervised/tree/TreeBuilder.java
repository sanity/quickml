package quickml.supervised.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.AttributesMapPredictiveModelBuilder;
import quickml.supervised.tree.treeBuildContexts.TreeContextBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 6/20/15.
 */

public interface TreeBuilder<P, I extends InstanceWithAttributesMap<?>> extends AttributesMapPredictiveModelBuilder<Tree<P>, I> {

    Tree<P> buildPredictiveModel(Iterable<I> trainingData);
    TreeBuilder<P, I> copy();
}
