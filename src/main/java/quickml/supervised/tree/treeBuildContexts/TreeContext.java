package quickml.supervised.tree.treeBuildContexts;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.bagging.Bagging;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.branchingConditions.BranchingConditions;

import java.util.List;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class TreeContext<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>, N extends Node<VC, N>> {

    public TreeContext(BranchingConditions<VC, N> branchingConditions, Scorer scorer,
                       List<? extends BranchFinderAndReducer<I, VC, N>> branchFindersAndReducers, LeafBuilder<VC, N> leafBuilder,
                       ValueCounterProducer<I, VC> valueCounterProducer) {
        this.scorer = scorer;
        this.branchFindersAndReducers = branchFindersAndReducers;
        this.leafBuilder = leafBuilder;
        this.branchingConditions = branchingConditions;
        this.valueCounterProducer = valueCounterProducer;
    }

    private Scorer scorer;
    private BranchingConditions branchingConditions;
    private final ValueCounterProducer<I, VC> valueCounterProducer;
    private Optional<? extends Bagging> bagging;
    private final List<? extends BranchFinderAndReducer<I, VC, N>> branchFindersAndReducers;
    private LeafBuilder<VC, N> leafBuilder;

    public LeafBuilder<VC, N> getLeafBuilder() {
        return leafBuilder;
    }

    public ValueCounterProducer<I, VC> getValueCounterProducer() {
        return valueCounterProducer;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public List<? extends BranchFinderAndReducer<I, VC, N>> getBranchFindersAndReducers() {
        return branchFindersAndReducers;
    }


    public BranchingConditions getBranchingConditions() {
        return branchingConditions;
    }

    public Optional<? extends Bagging> getBagging() {
        return bagging;
    }

}


    

