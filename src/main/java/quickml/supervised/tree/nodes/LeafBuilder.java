package quickml.supervised.tree.nodes;

import quickml.supervised.tree.branchSplitStatistics.ValueCounter;


/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface LeafBuilder<VC extends ValueCounter<VC>, N extends Node<VC, N>> {
    Leaf<VC, N> buildLeaf(Branch<VC, N> parent, VC valueCounter);
}
