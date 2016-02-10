package quickml.supervised.tree.regressionTree.treeBuildContexts;

import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducerFactory;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;
import quickml.supervised.tree.scorers.ScorerFactory;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;
import quickml.supervised.tree.treeBuildContexts.TreeContext;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public class RTreeContext<I extends RegressionInstance> extends TreeContext<I, MeanValueCounter> {

    public RTreeContext(BranchingConditions<MeanValueCounter> branchingConditions,
                        ScorerFactory<MeanValueCounter> scorerFactory,
                        List<BranchFinderAndReducerFactory<I, MeanValueCounter>> branchFindersAndReducers,
                        LeafBuilder<MeanValueCounter> leafBuilder,
                        ValueCounterProducer<I, MeanValueCounter> valueCounterProducer
    ) {
        super(branchingConditions, scorerFactory, branchFindersAndReducers, leafBuilder, valueCounterProducer);
    }

}
