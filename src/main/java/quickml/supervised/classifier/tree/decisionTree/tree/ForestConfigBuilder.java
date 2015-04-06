package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinderBuilder;

import static quickml.supervised.classifier.tree.decisionTree.tree.ForestOptions.*;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class ForestConfigBuilder<T extends InstanceWithAttributesMap, S extends SplitProperties, D extends DataProperties> {
    private Scorer scorer;
    private TerminationConditions<T,S> terminationConditions;
    private int numTrees = 1;
    private List<BranchFinderBuilder<T,D>> branchFinderBuilders = Lists.newArrayList();
    private LeafBuilder<T> leafBuilder;
    private DataPropertiesTransformer<T, S, D> dataPropertiesTransformer;
    private TreeFactory<D> treeFactory;
    private Optional<Bagging<T>> bagging;
    private Optional<Double> downSamplingTargetMinorityProportion = Optional.absent();
    private Optional<PostPruningStrategy<T>> pruningStrategy = Optional.absent();
    /*
    private int attributeValueObservationsThreshold = 0;  //goes in branchbuilder
    private double degreeOfGainRatioPenalty = 1.0; //goes in scorer
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5; // goes in the numeric branch builder
    private int samplesPerBin = 10; //goes in numeric branh builder
    */


    public Optional<Bagging<T>> getBagging() {
        return bagging;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public TerminationConditions<T, S> getTerminationConditions() {
        return terminationConditions;
    }

    public int getNumTrees() {
        return numTrees;
    }

    public List<BranchFinderBuilder<T, D>> getBranchFinderBuilders() {
        return branchFinderBuilders;
    }

    public LeafBuilder<T> getLeafBuilder() {
        return leafBuilder;
    }

    public TreeFactory<D> getTreeFactory() {
        return treeFactory;
    }

    public Optional<Double> getDownSamplingTargetMinorityProportion() {
        return downSamplingTargetMinorityProportion;
    }

    public Optional<PostPruningStrategy<T>> getPruningStrategy() {
        return pruningStrategy;
    }

    //builder setting methods
    public ForestConfigBuilder bagging(Bagging<T> bagging) {
        this.bagging = Optional.of(bagging);
        return this;
    }
    public ForestConfigBuilder downSamplingTargetMinorityProportion(Optional<Double> downSamplingTargetMinorityProportion) {
        this.downSamplingTargetMinorityProportion = downSamplingTargetMinorityProportion;
        return this;
    }
    public ForestConfigBuilder pruningStrategy(Optional<PostPruningStrategy<T>> pruningStrategy) {
        this.pruningStrategy = pruningStrategy;
        return this;
    }


    public ForestConfigBuilder leafBuilder(LeafBuilder leafBuilder) {
        this.leafBuilder = leafBuilder;
        return this;
    }

    public ForestConfigBuilder dataPropertiesTransformer(DataPropertiesTransformer<T, S, D> dataPropertiesTransformer) {
        this.dataPropertiesTransformer = dataPropertiesTransformer;
        return this;
    }

    public ForestConfigBuilder scorer(Scorer scorer) {
        this.scorer = scorer;
        return this;
    }


    public ForestConfigBuilder numTrees(int numTrees) {
        this.numTrees = numTrees;
        return this;
    }

    public ForestConfigBuilder BranchFinderBuilders(BranchFinderBuilder<T, D>... branchFinderBuilders ) {
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

    public ForestConfig<T, S, D> buildForestConfig(List<T> instances) {
       return dataPropertiesTransformer.createForestConfig(instances, this);
    }

    public ForestConfigBuilder<T, S, D> copy() {
        ForestConfigBuilder<T, S, D> copy = new ForestConfigBuilder<>();
        copy.dataPropertiesTransformer = dataPropertiesTransformer.copy();
        copy.terminationConditions = terminationConditions.copy();
        copy.branchFinderBuilders= copyBranchFinderBuilders();
        copy.treeFactory = treeFactory;
        copy.scorer = scorer;
        copy.numTrees = numTrees;
        copy.leafBuilder = leafBuilder;
        copy.pruningStrategy = pruningStrategy;
        copy.bagging = bagging;
        copy.downSamplingTargetMinorityProportion = downSamplingTargetMinorityProportion;

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
        if (cfg.containsKey(TREE_FACTORY.name()))
            treeFactory = (TreeFactory<D>) cfg.get(TREE_FACTORY.name());
        if (cfg.containsKey(NUM_TREES.name()))
            numTrees = (Integer) cfg.get(NUM_TREES.name());
        if (cfg.containsKey(BAGGING.name()))
            bagging = (Optional<Bagging<T>>) cfg.get(BAGGING.name());
        if (cfg.containsKey(DOWNSAMPLING_TARGET_MINORITY_PROPORTION.name()))
            downSamplingTargetMinorityProportion = (Optional<Double>) cfg.get(DOWNSAMPLING_TARGET_MINORITY_PROPORTION.name());
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
