package quickml.supervised.tree.configurations;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.google.common.collect.Sets;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.tree.bagging.Bagging;
import quickml.supervised.tree.bagging.StationaryBagging;
import quickml.supervised.tree.branchSplitStatistics.ValueCounter;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.branchFinders.BranchFinderBuilder;
import quickml.supervised.tree.terminationConditions.BranchingConditions;
import static quickml.supervised.tree.constants.ForestOptions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public abstract class TreeConfig<L, I extends InstanceWithAttributesMap<L>, VC extends ValueCounter<VC>, N extends Node<VC, N>> {  //specifically Tr must be of the same type as TreeConfig
    protected Scorer<VC> scorer;
    protected BranchingConditions<VC, N> branchingConditions;
    protected List<BranchFinderBuilder<VC, N>> branchFinderBuilders = Lists.newArrayList();
    protected LeafBuilder<VC, N> leafBuilder;
    protected Optional<? extends Bagging> bagging;

    public Set<BranchType> getBranchTypes() {
        Set<BranchType> branchTypes = Sets.newHashSet();
        for (BranchFinderBuilder<VC> branchFinderBuilder : branchFinderBuilders) {
            branchTypes.add(branchFinderBuilder.getBranchType());
        }
        return branchTypes;
    }

    public List<? extends BranchFinderBuilder<VC>> getBranchFinderBuilders() {
        return branchFinderBuilders;
    }

    public Optional<? extends Bagging> getBagging() {
        return bagging;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public BranchingConditions<VC> getBranchingConditions() {
        return branchingConditions;
    }
    

    public LeafBuilder<VC,N> getLeafBuilder() {
        return leafBuilder;
    }

    public TreeConfig<L, I, VC, N>  bagging(Bagging bagging) {
        this.bagging = Optional.of(bagging);
        return this;
    }

    public TreeConfig<L, I, VC, N>  bagging(boolean bagging) {
        this.bagging = Optional.of(new StationaryBagging());
        return this;
    }

    public TreeConfig<L, I, VC, N> scorer(Scorer scorer) {
        this.scorer = scorer;
        return this;
    }

    public TreeConfig<L, I, VC, N> branchFinderBuilders(BranchFinderBuilder<VC>... branchFinderFactories) {
        Preconditions.checkArgument(branchFinderFactories.length > 0, "must have at least one branch builder");
        this.branchFinderBuilders = Lists.newArrayList(branchFinderFactories);
        return this;
    }


    public TreeConfig<L, I, VC, N> terminationConditions(BranchingConditions<VC> branchingConditions) {
        this.branchingConditions = branchingConditions;
        return this;
    }

 public TreeConfig<L, I, VC, N> copy() {
        TreeConfig<L, I, VC, N> copy = new TreeConfig();
        List<BranchFinderBuilder<VC>> copiedBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<VC> branchFinderBuilder : this.branchFinderBuilders) {
            copiedBranchFinderBuilders.add(branchFinderBuilder.copy());
        }
        copy.branchFinderBuilders = copiedBranchFinderBuilders;
        copy.branchingConditions = branchingConditions.copy();
        copy.scorer = scorer;
        copy.leafBuilder = leafBuilder;
        copy.bagging = bagging;
        return copy;
    }
    
    public void update(final Map<String, Object> cfg) {
        for (BranchFinderBuilder<VC> branchFinderBuilder : branchFinderBuilders) {
            branchFinderBuilder.update(cfg);
        }
        if (cfg.containsKey(SCORER.name()))
            scorer = (Scorer) cfg.get(SCORER);
        if (cfg.containsKey(LEAF_BUILDER.name()))
            leafBuilder = (LeafBuilder<VC>) cfg.get(LEAF_BUILDER.name());
        if (cfg.containsKey(BAGGING.name()))
            bagging = (Optional<Bagging>) cfg.get(BAGGING.name());
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
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5; // goes in the numeric branch builder
    private int samplesPerBin = 10; //goes in numeric branh builder
    */

}
