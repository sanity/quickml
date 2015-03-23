package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.*;
import quickml.supervised.classifier.decisionTree.Scorer;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import static quickml.supervised.classifier.decisionTree.tree.ForestOptions.*;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class ForestConfigBuilder<T extends InstanceWithAttributesMap> {

    private int maxDepth = Integer.MAX_VALUE;
    private Scorer scorer;
    private double minScore=0;
    private int minLeafInstances = 0;
    private int numTrees = 1;
    public Optional<NumericBranchBuilder<T>> numericBranchBuilder = Optional.absent();
    public Optional<CategoricalBranchBuilder<T>> categoricalBranchBuilder = Optional.absent();
    public Optional<BooleanBranchBuilder<T>> booleanBranchBuilder = Optional.absent();
    private LeafBuilder<T> leafBuilder;
    private int attributeValueObservationsThreshold = 0;  //goes in branchbuilder
    private double degreeOfGainRatioPenalty = 1.0; //goes in scorer
    private AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    private AttributeIgnoringStrategy attributeIgnoringStrategy;
    private int binsForNumericSplits = 5; // goes in the numeric branch builder
    private int samplesPerBin = 10; //goes in numeric branh builder
    private DataPropertiesTransformer<T> dataPropertiesTransformer;

    //getters
    public int getMaxDepth() {
        return maxDepth;
    }

    public Scorer getScorer() {
        return scorer;
    }

    public double getMinScore() {
        return minScore;
    }

    public int getMinLeafInstances() {
        return minLeafInstances;
    }

    public int getNumTrees() {
        return numTrees;
    }

    public Optional<NumericBranchBuilder<T>> getNumericBranchBuilder() {
        return numericBranchBuilder;
    }

    public Optional<CategoricalBranchBuilder<T>> getCategoricalBranchBuilder() {
        return categoricalBranchBuilder;
    }

    public Optional<BooleanBranchBuilder<T>> getBooleanBranchBuilder() {
        return booleanBranchBuilder;
    }

    public LeafBuilder<T> getLeafBuilder() {
        return leafBuilder;
    }

    //builder setting methods
    public ForestConfigBuilder leafBuilder(LeafBuilder leafBuilder) {
        this.leafBuilder = leafBuilder;
        return this;
    }

    public ForestConfigBuilder dataProperties(DataPropertiesTransformer<T> dataProperties) {
        this.dataPropertiesTransformer = dataProperties;
        return this;
    }

    public ForestConfigBuilder maxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    public ForestConfigBuilder scorer(Scorer scorer) {
        this.scorer = scorer;
        return this;
    }

    public ForestConfigBuilder minScore(double minScore) {
        this.minScore = minScore;
        return this;
    }

    public ForestConfigBuilder minLeafInstances(int minLeafInstances) {
        this.minLeafInstances = minLeafInstances;
        return this;
    }

    public ForestConfigBuilder numTrees(int numTrees) {
        this.numTrees = numTrees;
        return this;
    }

    public ForestConfigBuilder attributeValueObservationsThreshold(int attributeValueObservationsThreshold) {
        this.attributeValueObservationsThreshold = attributeValueObservationsThreshold;
        return this;
    }

    public ForestConfigBuilder degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        return this;
    }

    public ForestConfigBuilder attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        this.attributeIgnoringStrategy = attributeIgnoringStrategy;
        return this;
    }

    public ForestConfigBuilder attributeValueIgnoringStrategy(AttributeValueIgnoringStrategy attributeValueIgnoringStrategy) {
        this.attributeValueIgnoringStrategy = attributeValueIgnoringStrategy;
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



    public ForestConfig<T> buildConfig(List<T> instances) {
       return dataPropertiesTransformer.createForestConfig(instances, this);

    }


    public ForestConfigBuilder<T> copy() {
        ForestConfigBuilder<T> copy = new ForestConfigBuilder<>();
        copy.dataPropertiesTransformer = dataPropertiesTransformer.copy();
        copy.attributeIgnoringStrategy = attributeIgnoringStrategy.copy();
        copy.attributeValueIgnoringStrategy = attributeValueIgnoringStrategy.copy();
        copy.scorer = scorer;
        copy.maxDepth = maxDepth;
        copy.minScore = minScore;
        copy.minLeafInstances = minLeafInstances;
        copy.booleanBranchBuilder = booleanBranchBuilder;
        copy.categoricalBranchBuilder = categoricalBranchBuilder;
        copy.numericBranchBuilder = numericBranchBuilder;
        copy.samplesPerBin = samplesPerBin;
        copy.binsForNumericSplits = binsForNumericSplits;
        copy.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
        copy.attributeValueObservationsThreshold = attributeValueObservationsThreshold;
        copy.numTrees = numTrees;
        copy.leafBuilder = leafBuilder;
        return copy;
    }

    public void update(final Map<String, Object> cfg) {
        if (cfg.containsKey(SCORER))
            scorer = (Scorer) cfg.get(SCORER);
        if (cfg.containsKey(SAMPLES_PER_BIN))
            samplesPerBin = (int) cfg.get(SAMPLES_PER_BIN);
        if (cfg.containsKey(BOOLEAN_BRANCH_BUILDER))
            booleanBranchBuilder = (Optional<BooleanBranchBuilder<T>>) cfg.get(BOOLEAN_BRANCH_BUILDER);
        if (cfg.containsKey(NUMERIC_BRANCH_BUILDER))
            numericBranchBuilder = (Optional<NumericBranchBuilder<T>>) cfg.get(NUMERIC_BRANCH_BUILDER);
        if (cfg.containsKey(CATEGORICAL_BRANCH_BUILDER))
            categoricalBranchBuilder = (Optional<CategoricalBranchBuilder<T>>) cfg.get(CATEGORICAL_BRANCH_BUILDER);
        if (cfg.containsKey(LEAF_BUILDER))
            leafBuilder = (LeafBuilder<T>) cfg.get(LEAF_BUILDER);
        if (cfg.containsKey(MAX_DEPTH))
            maxDepth = (Integer) cfg.get(MAX_DEPTH);
        if (cfg.containsKey(MIN_SCORE))
            minScore = (Double) cfg.get(MIN_SCORE);
        if (cfg.containsKey(NUM_TREES))
            minScore = (Double) cfg.get(numTrees);
        if (cfg.containsKey(MIN_LEAF_INSTANCES))
            minLeafInstances = (Integer) cfg.get(MIN_LEAF_INSTANCES);
        if (cfg.containsKey(BINS_FOR_NUMERIC_SPLITS))
            binsForNumericSplits = (Integer) cfg.get(BINS_FOR_NUMERIC_SPLITS);
        if (cfg.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY))
            degreeOfGainRatioPenalty = (Double) cfg.get(DEGREE_OF_GAIN_RATIO_PENALTY);
        if (cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY))
            attributeIgnoringStrategy= (AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_IGNORING_STRATEGY);
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY))
            attributeIgnoringStrategy= (AttributeIgnoringStrategy) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY);
        if (cfg.containsKey(ATTRIBUTE_VALUE_THRESHOLD_OBSERVATIONS))
            attributeValueObservationsThreshold = ((Integer) cfg.get(ATTRIBUTE_VALUE_THRESHOLD_OBSERVATIONS));
    }
}
