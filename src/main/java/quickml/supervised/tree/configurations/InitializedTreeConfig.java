package quickml.supervised.tree.configurations;

import com.google.common.base.Optional;
import quickml.supervised.tree.bagging.Bagging;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.scorers.Scorer;

import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.terminationConditions.TerminationConditions;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class InitializedTreeConfig<TS extends ValueCounter<TS>, D extends DataProperties> {
//option here: if data properties only needed in service of intitializing the Attr. ignoring strategy, and the termination conditions, then only need d
    //in configurations...have a protected abstract method that initializes these objects we actually want to pass on, and in the constructor of the implementing class, we can initialize a field (the user doesn't
    //know about) we can create a data properties object that takes care of this for us.


    public InitializedTreeConfig(TerminationConditions<TS> terminationConditions, Scorer scorer,
                                 Iterable<BranchFinder<TS>> branchFinders, LeafBuilder<TS> leafBuilder, Optional<? extends Bagging> bagging, D dataProperties

                                 ) {
        this.scorer = scorer;
        this.branchFinders = branchFinders;
        this.leafBuilder = leafBuilder;
        this.terminationConditions = terminationConditions;
        this.bagging = bagging;
        this.dataProperties = dataProperties;
    }

    private Scorer scorer;
    private TerminationConditions terminationConditions;
    private Optional<? extends Bagging> bagging;
    private D dataProperties;
    private Iterable<BranchFinder<TS>> branchFinders;
    private LeafBuilder<TS> leafBuilder;



    public D getDataProperties() { return dataProperties; }

    public LeafBuilder<TS> getLeafBuilder() {
        return leafBuilder;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public Iterable<BranchFinder<TS> >getBranchFinders(){
        return branchFinders;
    }


    public TerminationConditions getTerminationConditions() {
        return terminationConditions;
    }

    public Optional<? extends Bagging> getBagging() {
        return bagging;
    }

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
