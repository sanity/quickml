package quickml.supervised.tree.nodes;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;


/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface LeafBuilder<VC extends ValueCounter<VC>> extends Serializable{
    Leaf<VC> buildLeaf(Branch<VC> parent, VC valueCounter);
    LeafBuilder<VC> copy();
}
