package quickml.supervised.tree.nodes;


import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 4/24/15.
 */

public interface Leaf<VC extends ValueCounter<VC>> extends Node<VC> {
    int getDepth();
    VC getValueCounter();
}
