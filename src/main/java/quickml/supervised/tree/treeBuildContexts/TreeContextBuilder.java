package quickml.supervised.tree.treeBuildContexts;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.google.common.collect.Sets;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounterProducer;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import static quickml.supervised.tree.constants.ForestOptions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public abstract class TreeContextBuilder<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>> {
    protected Scorer<VC> scorer;
    protected LeafBuilder<VC, N> leafBuilder;
    protected ValueCounterProducer<L, I, VC> valueCounterProducer;
    protected BranchingConditions<VC, N> branchingConditions;
    protected List<BranchFinderBuilder<VC, N>> branchFinderBuilders = Lists.newArrayList();


    public ValueCounterProducer<L, I, VC> getValueCounterProducer() {
        return valueCounterProducer;
    }

    public Set<BranchType> getBranchTypes() {
        Set<BranchType> branchTypes = Sets.newHashSet();
        for (BranchFinderBuilder<VC, N> branchFinderBuilder : branchFinderBuilders) {
            branchTypes.add(branchFinderBuilder.getBranchType());
        }
        return branchTypes;
    }

    public List<? extends BranchFinderBuilder<VC, N>> getBranchFinderBuilders() {
        return branchFinderBuilders;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public BranchingConditions<VC, N> getBranchingConditions() {
        return branchingConditions;
    }


    public LeafBuilder<VC,N> getLeafBuilder() {
        return leafBuilder;
    }
   //perhaps make part of training data reducer as a default method...no need to set here separately
    public TreeContextBuilder<L, I, VC, N> aggregateStatistics(ValueCounterProducer<L, I, VC> valueCounterProducer) {
        this.valueCounterProducer = valueCounterProducer;
        return this;
    }


    public TreeContextBuilder<L, I, VC, N> scorer(Scorer scorer) {
        this.scorer = scorer;
        return this;
    }

    public TreeContextBuilder<L, I, VC, N> branchFinderBuilders(BranchFinderBuilder<VC, N>... branchFinderFactories) {
        Preconditions.checkArgument(branchFinderFactories.length > 0, "must have at least one branch builder");
        this.branchFinderBuilders = Lists.newArrayList(branchFinderFactories);
        return this;
    }

    public TreeContextBuilder<L, I, VC, N> terminationConditions(BranchingConditions<VC, N> branchingConditions) {
        this.branchingConditions = branchingConditions;
        return this;
    }

 public TreeContextBuilder<L, I, VC, N> copy() {
        TreeContextBuilder<L, I, VC, N> copy = createTreeBuildContext();
        List<BranchFinderBuilder<VC, N>> copiedBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<VC, N> branchFinderBuilder : this.branchFinderBuilders) {
            copiedBranchFinderBuilders.add(branchFinderBuilder.copy());
        }
        copy.branchFinderBuilders = copiedBranchFinderBuilders;
        copy.branchingConditions = branchingConditions.copy();
        copy.scorer = scorer;
        copy.leafBuilder = leafBuilder;
        return copy;
    }

    public boolean hasBranchFinderBuilder(BranchType branchType) {
        return getBranchFinderBuilder(branchType).isPresent();
    }

    public Optional<? extends BranchFinderBuilder<VC, N>> getBranchFinderBuilder(BranchType branchType) {
        for (BranchFinderBuilder branchFinderBuilder : branchFinderBuilders) {
            if (branchFinderBuilder.getBranchType().equals(branchType)) {
                return Optional.of(branchFinderBuilder);
            }
        }
        return Optional.absent();
    }

    public abstract TreeContextBuilder<L, I, VC, N> createTreeBuildContext();

    public abstract TreeContext<L, I, VC, N> buildContext(List<I> trainingData);

    public void update(final Map<String, Object> cfg) {
        for (BranchFinderBuilder<VC, N> branchFinderBuilder : branchFinderBuilders) {
            branchFinderBuilder.update(cfg);
        }
        if (cfg.containsKey(LEAF_BUILDER.name()))
            leafBuilder = (LeafBuilder<VC, N>) cfg.get(LEAF_BUILDER.name());
        if (cfg.containsKey(SCORER.name()))
            scorer = (Scorer) cfg.get(SCORER.name());
        scorer.update(cfg);
        branchingConditions.update(cfg);

        /*
        if (cfg.containsKey(BINS_FOR_NUMERIC_SPLITS))
            binsForNumericSplits = (Integer) cfg.get(BINS_FOR_NUMERIC_SPLITS);
        if (cfg.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY))
            degreeOfGainRatioPenalty = (Double) cfg.get(DEGREE_OF_GAIN_RATIO_PENALTY);

        if (cfg.containsKey(SAMPLES_PER_BIN))
            samplesPerBin = (int) cfg.get(SAMPLES_PER_BIN);
           */

        //branchFinderBuilders had many properties held
    }
    
    /*    public StateOfTreeBuild<VC, D> createTreeBuildContext(Bagging.TrainingDataPair<L, I> trainingDataPair, TreeConfig<VC, D> fcb) {
        D dataProperties = getDataProperties(trainingDataPair.trainingData, fcb.getBranchTypes());
        List<BranchFinder<VC, N>> initializedBranchFinders = initializeBranchFinders(fcb, dataProperties);
        return new StateOfTreeBuild<VC, D>(fcb.getBranchingConditions(), fcb.getScorer(), initializedBranchFinders,
                fcb.buildLeaf(), fcb.getBagging(), dataProperties, trainingDataPair.outOfBagTrainingData);
    }
    */
    

/*

    public ForestConfigBuilder degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        return this;
    }



    public ForestConfigBuilder binsForNumericSplits(int binsForNumericSplits) {
        this.binsForNumericSplits = binsForNumericSplits;
        return this;
    }

    public ForestConfigBuilder samplesPerBin(int samplesPerBin) {
        this.samplesPerBin = samplesPerBin;
        return this;
    }

    public ForestConfigBuilder numericBranchBuilder(NumericBranchBuilder<T> numericBranchBuilder) {
        this.numericBranchBuilder = Optional.of(numericBranchBuilder);
        return this;
    }

    public ForestConfigBuilder categoricalBranchBuilder(CategoricalBranchBuilder<T> categoricalBranchBuilder) {
        this.categoricalBranchBuilder = Optional.of(categoricalBranchBuilder);
        return this;
    }


    public ForestConfigBuilder booleanBranchBuilder(BooleanBranchBuilder<T> booleanBranchBuilder) {
        this.booleanBranchBuilder = Optional.of(booleanBranchBuilder);
        return this;
    }

*/

    /*
    private int attributeValueObservationsThreshold = 0;  //goes in branchbuilder
    private double degreeOfGainRatioPenalty = 1.0; //goes in scorer
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategies;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5; // goes in the numeric branch builder
    private int samplesPerBin = 10; //goes in numeric branh builder
    */

}
