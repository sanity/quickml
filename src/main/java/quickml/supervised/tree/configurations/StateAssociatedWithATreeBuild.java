package quickml.supervised.tree.configurations;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.bagging.Bagging;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.Scorer;

import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.terminationConditions.BranchingConditions;

import java.util.List;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public abstract class StateAssociatedWithATreeBuild<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>>{

    public StateAssociatedWithATreeBuild(BranchingConditions<VC, N> branchingConditions, Scorer scorer,
                                         Iterable<BranchFinder<VC, N>> branchFinders, LeafBuilder<VC, N> leafBuilder, Optional<? extends Bagging> bagging) {
        this.scorer = scorer;
        this.branchFinders = branchFinders;
        this.leafBuilder = leafBuilder;
        this.branchingConditions = branchingConditions;
        this.bagging = bagging;
    }

    private Scorer scorer;
    private BranchingConditions branchingConditions;
    private Optional<? extends Bagging> bagging;
    private Iterable<BranchFinder<VC, N>> branchFinders;
    private LeafBuilder<VC> leafBuilder;
    private List<I> outOfBagData;

    public LeafBuilder<VC> getLeafBuilder() {
        return leafBuilder;
    }

    public List<I> getOutOfBagData() {
        return outOfBagData;
    }

    public abstract Object getDataProperties();


    public Scorer getScorer() {
        return scorer;
    }

    public Iterable<BranchFinder<VC, N> >getBranchFinders(){
        return branchFinders;
    }


    public BranchingConditions getBranchingConditions() {
        return branchingConditions;
    }

    public Optional<? extends Bagging> getBagging() {
        return bagging;
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
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5;
    private int samplesPerBin = 10;
    private ForestConfig(Map<ForestOptions, Object> cfg) {
        update(cfg);
    }
    */
    
}
