package quickml.supervised.tree.nodes;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 6/10/15.
 */
//for later refactor
public class BranchAndProperties<TS extends ValueCounter<TS>> {
    public final double probabilityOfTrueChild;
    public final double score;
    public final int depth;
    public final Branch<TS> branch;

    public BranchAndProperties(double probabilityOfTrueChild, int depth, Branch<TS> branch, double score) {
        this.probabilityOfTrueChild = probabilityOfTrueChild;
        this.depth = depth;
        this.branch = branch;
        this.score = score;
    }
}
