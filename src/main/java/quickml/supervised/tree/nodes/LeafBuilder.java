package quickml.supervised.tree.nodes;

import quickml.supervised.tree.summaryStatistics.ValueCounter;


/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface LeafBuilder<VC extends ValueCounter<VC>> {
    Leaf<VC> buildLeaf(Branch<VC> parent, VC valueCounter);
}
