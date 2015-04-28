package quickml.supervised.tree.nodes;

import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;

/**
 * Created by alexanderhawk on 4/27/15.
 */
public class DTCatBranch<TS extends TermStatsAndOperations<TS>> extends Branch<TS> implements DTNode {
    public void dump(final Appendable ap) {
        dump(0, ap);
    }
}
