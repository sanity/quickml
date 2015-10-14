package quickml.supervised.tree.treeBuildContexts;

import com.google.common.base.Optional;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.tree.bagging.Bagging;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducerFactory;
import quickml.supervised.tree.scorers.ScorerFactory;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.branchingConditions.BranchingConditions;

import java.util.List;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class TreeContext<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>> {

    public TreeContext(BranchingConditions<VC> branchingConditions, ScorerFactory<VC> scorerFactory,
                       List<? extends BranchFinderAndReducerFactory<I, VC>> branchFindersAndReducers, LeafBuilder<VC> leafBuilder,
                       ValueCounterProducer<I, VC> valueCounterProducer) {
        this.scorerFactory = scorerFactory;
        this.branchFindersAndReducers = branchFindersAndReducers;
        this.leafBuilder = leafBuilder;
        this.branchingConditions = branchingConditions;
        this.valueCounterProducer = valueCounterProducer;
    }

    private ScorerFactory<VC> scorerFactory;
    private BranchingConditions branchingConditions;
    private final ValueCounterProducer<I, VC> valueCounterProducer;
    private Optional<? extends Bagging> bagging;
    private final List<? extends BranchFinderAndReducerFactory<I, VC>> branchFindersAndReducers;
    private LeafBuilder<VC> leafBuilder;

    public LeafBuilder<VC> getLeafBuilder() {
        return leafBuilder;
    }

    public ValueCounterProducer<I, VC> getValueCounterProducer() {
        return valueCounterProducer;
    }

    public ScorerFactory<VC> getScorerFactory() {
        return scorerFactory;
    }

    public List<? extends BranchFinderAndReducerFactory<I, VC>> getBranchFindersAndReducers() {
        return branchFindersAndReducers;
    }


    public BranchingConditions getBranchingConditions() {
        return branchingConditions;
    }

    public Optional<? extends Bagging> getBagging() {
        return bagging;
    }

}


    

