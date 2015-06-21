package quickml.supervised.tree;

import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 4/3/15.
 */


public interface Tree<P> extends PredictiveModel<AttributesMap, P> {
}
