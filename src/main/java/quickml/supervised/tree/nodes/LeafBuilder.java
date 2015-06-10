package quickml.supervised.tree.nodes;

import quickml.supervised.tree.branchSplitStatistics.TermStatsAndOperations;


/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface LeafBuilder<TS extends TermStatsAndOperations<TS>> {
    Leaf<TS> buildLeaf(Branch parent, TS termStatistics);
}
