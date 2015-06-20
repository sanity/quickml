package quickml.supervised.tree.branchFinders;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.TrainingDataReducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.nodes.Node;

/**
 * Created by alexanderhawk on 6/18/15.
 */
public class BranchFinderAndReducer <L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>> {
    protected BranchFinder<VC, N> branchFinder;
    protected TrainingDataReducer<L, I, VC> reducer;

    public BranchFinderAndReducer(BranchFinder<VC, N> branchFinder, TrainingDataReducer<L, I, VC> reducer) {
        this.branchFinder = branchFinder;
        this.reducer = reducer;
    }

    public BranchFinder<VC, N> getBranchFinder() {
        return branchFinder;
    }

    public TrainingDataReducer<L, I, VC> getReducer() {
        return reducer;
    }
}
