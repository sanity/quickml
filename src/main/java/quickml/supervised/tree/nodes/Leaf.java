package quickml.supervised.tree.nodes;


import quickml.supervised.tree.branchSplitStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 4/24/15.
 */

//signature ensures that Leaf<TS, N> extends N (as it extends Node<TS, N>, which has exactly one implementing class: N).
public interface Leaf<VC extends ValueCounter<VC>, N extends Node<VC, N>> extends Node<VC, N> {
    public abstract int getDepth();
    public abstract VC getTermStats();
}
