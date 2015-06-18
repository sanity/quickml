package quickml.supervised.tree.nodes;

import quickml.data.AttributesMap;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 6/18/15.
 */
public interface Node<VC extends ValueCounter<VC>, N extends Node<VC, N>> extends NodeBase<VC> {
    Leaf<VC, N> getLeaf(AttributesMap attributes);
    N getParent();
}
