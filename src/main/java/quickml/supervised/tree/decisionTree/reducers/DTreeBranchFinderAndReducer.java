package quickml.supervised.tree.decisionTree.reducers;

import quickml.data.ClassifierInstance;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducer;
import quickml.supervised.tree.decisionTree.nodes.DTNode;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.Reducer;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public class DTreeBranchFinderAndReducer<I extends ClassifierInstance> extends BranchFinderAndReducer<I, ClassificationCounter, DTNode>{
    protected DTreeReducer<I> reducer;

    public DTreeBranchFinderAndReducer(BranchFinder<ClassificationCounter, DTNode> branchFinder, DTreeReducer<I> reducer) {
        super(branchFinder, reducer);
        reducer = reducer;
    }

    @Override
    public DTreeReducer<I> getReducer() {
        return reducer;
    }
}
