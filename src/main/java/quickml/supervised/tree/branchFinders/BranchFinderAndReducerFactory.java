package quickml.supervised.tree.branchFinders;

import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.tree.reducers.ReducerFactory;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 6/18/15.
 */
public class BranchFinderAndReducerFactory<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>> {
    protected BranchFinder<VC> branchFinder;
    protected ReducerFactory<I, VC> reducerFactory;

    public BranchFinderAndReducerFactory(BranchFinder<VC> branchFinder, ReducerFactory<I, VC> reducerFactory) {
        this.branchFinder = branchFinder;
        this.reducerFactory = reducerFactory;
    }

    public BranchFinder<VC> getBranchFinder() {
        return branchFinder;
    }

    public ReducerFactory<I, VC> getReducerFactory() {
        return reducerFactory;
    }
}
