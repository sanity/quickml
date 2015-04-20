package quickml.supervised.classifier.tree.decisionTree.tree;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders.BranchFinderBuilder;

import static quickml.supervised.classifier.tree.decisionTree.tree.ForestOptions.*;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class TreeConfig<GS extends TermStatistics> {  //specifically Tr must be of the same type as TreeConfig
    private Scorer<GS> scorer;
    private TerminationConditions<GS> terminationConditions;
    private List<BranchFinderBuilder<GS>> branchFinderBuilders = Lists.newArrayList();
    private LeafBuilder<GS> leafBuilder;
    private Optional<Bagging> bagging;
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

    public TerminationConditions<GS> getTerminationConditions() {
        return terminationConditions;
    }

    public List<BranchFinderBuilder<GS>> getBranchFinderBuilders() {
        return branchFinderBuilders;
    }

    public LeafBuilder<GS> getLeafBuilder() {
        return leafBuilder;
    }

//    protected abstract TreeConfig<GS> newTreeConfig();



    //builder setting methods
    public TreeConfig<GS>  bagging(Bagging bagging) {
        this.bagging = Optional.of(bagging);
        return this;
    }


    public TreeConfig<GS> scorer(Scorer scorer) {
        this.scorer = scorer;
        return this;
    }


    public TreeConfig<GS>  BranchFinderBuilders(BranchFinderBuilder<GS>... branchFinderBuilders ) {
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
    public TreeConfig<GS> copy() {
        TreeConfig<GS> copy = new TreeConfig();
        copy.terminationConditions = terminationConditions.copy();
        copy.branchFinderBuilders= copyBranchFinderBuilders();
        copy.scorer = scorer;
        copy.leafBuilder = leafBuilder;
        copy.bagging = bagging;
        return copy;
    }

    private List<BranchFinderBuilder<GS>>  copyBranchFinderBuilders() {
        List<BranchFinderBuilder<GS>> newBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<GS> BranchFinderBuilder : branchFinderBuilders) {
            newBranchFinderBuilders.add(BranchFinderBuilder.copy());
        }
        return newBranchFinderBuilders;
    }

    public void update(final Map<String, Object> cfg) {
        if (cfg.containsKey(SCORER))
            scorer = (Scorer) cfg.get(SCORER);
        for (BranchFinderBuilder<GS> BranchFinderBuilder : branchFinderBuilders) {
            BranchFinderBuilder.update(cfg);
        }
        if (cfg.containsKey(LEAF_BUILDER.name()))
            leafBuilder = (LeafBuilder<GS>) cfg.get(LEAF_BUILDER.name());
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
    }
}
