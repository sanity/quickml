package quickml.supervised.tree.nodes;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 6/10/15.
 */
//for later refactor
public class BranchAndProperties<VC extends ValueCounter<VC>, N extends Node<VC,N>> {
    public final double probabilityOfTrueChild;
    public final double score;
    public final int depth;
    public final Branch<VC, N> branch;

    public BranchAndProperties(double probabilityOfTrueChild, int depth, Branch<VC, N> branch, double score) {
        this.probabilityOfTrueChild = probabilityOfTrueChild;
        this.depth = depth;
        this.branch = branch;
        this.score = score;
    }
}
