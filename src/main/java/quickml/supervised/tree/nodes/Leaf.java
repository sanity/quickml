package quickml.supervised.tree.nodes;


import quickml.supervised.tree.branchSplitStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 4/24/15.
 */
public interface Leaf<TS extends ValueCounter<TS>> extends Node<TS> {
    public abstract int getDepth();
    public abstract TS getTermStats();
}
