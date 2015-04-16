package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinderBuilder;

import static quickml.supervised.classifier.tree.decisionTree.tree.ForestOptions.*;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public abstract class TreeConfig<L, T extends InstanceWithAttributesMap<L>, GS extends GroupStatistics, Tr extends TreeConfig<L, T, GS, Tr >> {  //specifically Tr must be of the same type as TreeConfig
   //notes: pull the implementation of dataProperties transformer into the intitialize method
    private Scorer<GS> scorer;
    //how do i promise to have a termination conditions class
    private TerminationConditions<L, T, GS> terminationConditions;
    private List<BranchFinderBuilder<L, T, Tr>> branchFinderBuilders = Lists.newArrayList();
    private LeafBuilder<GS> leafBuilder;
    private Optional<Bagging> bagging;
    private Optional<PostPruningStrategy<T>> pruningStrategy = Optional.absent();
    /*
    private int attributeValueObservationsThreshold = 0;  //goes in branchbuilder
    private double degreeOfGainRatioPenalty = 1.0; //goes in scorer
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5; // goes in the numeric branch builder
    private int samplesPerBin = 10; //goes in numeric branh builder
    */


    public Optional<Bagging> getBagging() {
        return bagging;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public TerminationConditions<L, T, GS> getTerminationConditions() {
        return terminationConditions;
    }

    public List<BranchFinderBuilder<L, T, Tr>> getBranchFinderBuilders() {
        return branchFinderBuilders;
    }

    public LeafBuilder<GS> getLeafBuilder() {
        return leafBuilder;
    }


    public Optional<PostPruningStrategy<T>> getPruningStrategy() {
        return pruningStrategy;
    }

    //builder setting methods
    public TreeConfig bagging(Bagging bagging) {
        this.bagging = Optional.of(bagging);
        return this;
    }

    public TreeConfig<L, T, GS, Tr> pruningStrategy(Optional<PostPruningStrategy<T>> pruningStrategy) {
        this.pruningStrategy = pruningStrategy;
        return this;
    }


    public TreeConfig<L, T, GS, Tr> leafBuilder(LeafBuilder leafBuilder) {
        this.leafBuilder = leafBuilder;
        return this;
    }

    public TreeConfig<L, T, GS, Tr> dataPropertiesTransformer(DataPropertiesTransformer<T, S, D> dataPropertiesTransformer) {
        this.dataPropertiesTransformer = dataPropertiesTransformer;
        return this;
    }

    public TreeConfig<L, T, GS, Tr> scorer(Scorer scorer) {
        this.scorer = scorer;
        return this;
    }


    public TreeConfig BranchFinderBuilders(BranchFinderBuilder<L, T, Tr>... branchFinderBuilders ) {
        Preconditions.checkArgument(branchFinderBuilders.length > 0, "must have at least one branch builder");
        this.branchFinderBuilders = Lists.newArrayList(branchFinderBuilders);
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

    public abstract InitializedTreeConfig<T, S, D> buildForestConfig(List<T> instances);

    public TreeConfig<L, T, GS, Tr> copy() {
        TreeConfig<T, S, D> copy = new TreeConfig<>();
        copy.dataPropertiesTransformer = dataPropertiesTransformer.copy();
        copy.terminationConditions = terminationConditions.copy();
        copy.branchFinderBuilders= copyBranchFinderBuilders();
        copy.scorer = scorer;
        copy.leafBuilder = leafBuilder;
        copy.pruningStrategy = pruningStrategy;
        copy.bagging = bagging;

        return copy;
    }

    private List<BranchFinderBuilder<T,D>>  copyBranchFinderBuilders() {
        List<BranchFinderBuilder<T,D>> newBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<T,D> BranchFinderBuilder : branchFinderBuilders) {
            newBranchFinderBuilders.add(BranchFinderBuilder.copy());
        }
        return newBranchFinderBuilders;
    }

    public void update(final Map<String, Object> cfg) {
        if (cfg.containsKey(SCORER))
            scorer = (Scorer) cfg.get(SCORER);
        for (BranchFinderBuilder<T,D> BranchFinderBuilder : branchFinderBuilders) {
            BranchFinderBuilder.update(cfg);
        }
        if (cfg.containsKey(LEAF_BUILDER.name()))
            leafBuilder = (LeafBuilder<T>) cfg.get(LEAF_BUILDER.name());
        if (cfg.containsKey(BAGGING.name()))
            bagging = (Optional<Bagging<T>>) cfg.get(BAGGING.name());

        if (cfg.containsKey(PRUNING_STRATEGY.name()))
            pruningStrategy = (Optional<PostPruningStrategy<T>>) cfg.get(PRUNING_STRATEGY.name());
        if (cfg.containsKey(DATA_PROPERTIES_TRANSFORMER))
            dataPropertiesTransformer = (DataPropertiesTransformer<T, S, D>)cfg.get(DATA_PROPERTIES_TRANSFORMER);

        terminationConditions.update(cfg);

        /*
        if (cfg.containsKey(BINS_FOR_NUMERIC_SPLITS))
            binsForNumericSplits = (Integer) cfg.get(BINS_FOR_NUMERIC_SPLITS);
        if (cfg.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY))
            degreeOfGainRatioPenalty = (Double) cfg.get(DEGREE_OF_GAIN_RATIO_PENALTY);

        if (cfg.containsKey(SAMPLES_PER_BIN))
            samplesPerBin = (int) cfg.get(SAMPLES_PER_BIN);
           */
    }
}
