package quickml.supervised.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.AttributesMapPredictiveModelBuilder;

import java.util.Map;

/**
 * Created by alexanderhawk on 6/20/15.
 */

public interface TreeBuilder<P, L, I extends InstanceWithAttributesMap<L>> extends AttributesMapPredictiveModelBuilder<P, Tree<P>, L, I> {

    public abstract Tree<P> buildPredictiveModel(Iterable<I> trainingData);

}
