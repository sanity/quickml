package quickml.supervised.tree.nodes;


import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;

/**
 * Created by alexanderhawk on 4/24/15.
 */
public interface Leaf<TS extends TermStatsAndOperations<TS>> extends Node<TS> {
    public abstract int getDepth();
    public abstract TS getTermStats();
}
