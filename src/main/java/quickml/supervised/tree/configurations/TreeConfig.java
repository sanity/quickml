package quickml.supervised.tree.configurations;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import com.google.common.collect.Sets;
import quickml.supervised.tree.completeDataSetSummaries.DataProperties;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.branchFinders.BranchFinderBuilder;
import quickml.supervised.tree.branchFinders.TermStatsAndOperations;

import static quickml.supervised.tree.decisionTree.tree.ForestOptions.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class TreeConfig<TS extends TermStatsAndOperations<TS>, D extends DataProperties> {  //specifically Tr must be of the same type as TreeConfig
    protected Scorer<TS> scorer;
    protected TerminationConditions<TS> terminationConditions;
    protected List<BranchFinderBuilder<TS, D>> branchFinderBuilders = Lists.newArrayList();
    protected LeafBuilder<TS> leafBuilder;
    protected Optional<? extends Bagging> bagging;

    /*
    private int attributeValueObservationsThreshold = 0;  //goes in branchbuilder
    private double degreeOfGainRatioPenalty = 1.0; //goes in scorer
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5; // goes in the numeric branch builder
    private int samplesPerBin = 10; //goes in numeric branh builder
    */

    public Set<BranchType> getBranchTypes() {
        Set<BranchType> branchTypes = Sets.newHashSet();
        for (BranchFinderBuilder<TS, D> branchFinderBuilder : branchFinderBuilders) {
            branchTypes.add(branchFinderBuilder.getBranchType());
        }
        return branchTypes;
    }

    public List<? extends BranchFinderBuilder<TS, D>> getBranchFinderBuilders() {
        return branchFinderBuilders;
    }

    public Optional<Bagging> getBagging() {
        return bagging;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public TerminationConditions<TS> getTerminationConditions() {
        return terminationConditions;
    }
    

    public LeafBuilder<TS> getLeafBuilder() {
        return leafBuilder;
    }

    public TreeConfig<TS, D>  bagging(Bagging bagging) {
        this.bagging = Optional.of(bagging);
        return this;
    }

    public TreeConfig<TS, D>  bagging(boolean bagging) {
        this.bagging = Optional.of(new StationaryBagging());
        return this;
    }

    public TreeConfig<TS, D> scorer(Scorer scorer) {
        this.scorer = scorer;
        return this;
    }

    public TreeConfig<TS, D> branchFinderBuilders(BranchFinderBuilder<TS, D>... branchFinderFactories) {
        Preconditions.checkArgument(branchFinderFactories.length > 0, "must have at least one branch builder");
        this.branchFinderBuilders = Lists.newArrayList(branchFinderFactories);
        return this;
    }


    public TreeConfig<TS, D> terminationConditions(TerminationConditions<TS> terminationConditions) {
        this.terminationConditions = terminationConditions;
        return this;
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
    public TreeConfig<TS, D> copy() {
        TreeConfig<TS, D> copy = new TreeConfig();
        List<BranchFinderBuilder<TS, D>> copiedBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<TS, D> branchFinderBuilder : this.branchFinderBuilders) {
            copiedBranchFinderBuilders.add(branchFinderBuilder.copy());
        }
        copy.branchFinderBuilders = copiedBranchFinderBuilders;
        copy.terminationConditions = terminationConditions.copy();
        copy.scorer = scorer;
        copy.leafBuilder = leafBuilder;
        copy.bagging = bagging;
        return copy;
    }
    
    public void update(final Map<String, Object> cfg) {
        for (BranchFinderBuilder<TS, D> branchFinderBuilder : branchFinderBuilders) {
            branchFinderBuilder.update(cfg);
        }
        if (cfg.containsKey(SCORER))
            scorer = (Scorer) cfg.get(SCORER);
        if (cfg.containsKey(LEAF_BUILDER.name()))
            leafBuilder = (LeafBuilder<TS>) cfg.get(LEAF_BUILDER.name());
        if (cfg.containsKey(BAGGING.name()))
            bagging = (Optional<Bagging>) cfg.get(BAGGING.name());
        terminationConditions.update(cfg);

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
}
