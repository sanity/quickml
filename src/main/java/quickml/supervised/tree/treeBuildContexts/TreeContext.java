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
public class TreeContext<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>> {

    public TreeContext(BranchingConditions<VC, N> branchingConditions, Scorer scorer,
                       List<? extends BranchFinderAndReducer<L, I, VC, N>> branchFindersAndReducers, LeafBuilder<VC, N> leafBuilder,
                       ValueCounterProducer<L, I, VC> valueCounterProducer) {
        this.scorer = scorer;
        this.branchFindersAndReducers = branchFindersAndReducers;
        this.leafBuilder = leafBuilder;
        this.branchingConditions = branchingConditions;
        this.valueCounterProducer = valueCounterProducer;
    }

    private Scorer scorer;
    private BranchingConditions branchingConditions;
    private final ValueCounterProducer<L, I, VC> valueCounterProducer;
    private Optional<? extends Bagging> bagging;
    private final List<? extends BranchFinderAndReducer<L, I, VC, N>> branchFindersAndReducers;
    private LeafBuilder<VC, N> leafBuilder;

    public LeafBuilder<VC, N> getLeafBuilder() {
        return leafBuilder;
    }

    public ValueCounterProducer<L, I, VC> getValueCounterProducer() {
        return valueCounterProducer;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public List<? extends BranchFinderAndReducer<L, I, VC, N>> getBranchFindersAndReducers() {
        return branchFindersAndReducers;
    }


    public BranchingConditions getBranchingConditions() {
        return branchingConditions;
    }

    public Optional<? extends Bagging> getBagging() {
        return bagging;
    }

}

    /*
        public boolean isInvalidSplit(double score){
        return branchingConditions.isInvalidSplit(score);
    }

    public boolean isInvalidSplit(VC trueValueStats, VC falseValueStats){
        return branchingConditions.isInvalidSplit(trueValueStats, falseValueStats);
    }

    public boolean canTryAddingChildren(Branch<VC, N> branch, VC VC) {
        branchingConditions.canTryAddingChildren(branch, VC);
    }
    */


  /*
    //put in specific branch builders
    private int attributeValueObservationsThreshold = 0;
    private double degreeOfGainRatioPenalty = 1.0;
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategies;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5;
    private int samplesPerBin = 10;
    private ForestConfig(Map<ForestOptions, Object> cfg) {
        update(cfg);
    }
    */
    

