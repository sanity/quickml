package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Optional;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.*;
import quickml.supervised.classifier.decisionTree.Scorer;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.decisionTree.tree.ForestOptions.*;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class ForestConfig<T extends InstanceWithAttributesMap> {


    public Scorer scorer;
    public double minScore=0;
    public int minLeafInstances = 0;
    public int numTrees = 1;
    public int attributeValueObservationsThreshold = 0;
    public double degreeOfGainRatioPenalty = 1.0;
    public AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;
    public AttributeIgnoringStrategy attributeIgnoringStrategy;
    public int binsForNumericSplits = 5;
    public int samplesPerBin = 10;
    public boolean buildClassificationTrees = false;
    public Optional<NumericBranchBuilder<T>> numericBranchBuilder = Optional.absent();
    public Optional<CategoricalBranchBuilder<T>> categoricalBranchBuilder = Optional.absent();
    public Optional<BooleanBranchBuilder<T>> booleanBranchBuilder = Optional.absent();
    public int maxDepth = Integer.MAX_VALUE;
    public ForestConfig(Map<ForestOptions, Object> cfg) {
        update(cfg);
    }


    //do we need this copy method? possibly for scaling up to more machines
    public void copyForestConfig(ForestConfigBuilder<T> forestConfig) {
        attributeIgnoringStrategy = forestConfig.attributeIgnoringStrategy.copy();
        attributeValueIgnoringStrategy = forestConfig.attributeValueIgnoringStrategy.copy();
        scorer = forestConfig.scorer;
        maxDepth = forestConfig.maxDepth;
        minScore = forestConfig.minScore;
        minLeafInstances = forestConfig.minLeafInstances;
        booleanBranchBuilder = forestConfig.booleanBranchBuilder;
        categoricalBranchBuilder = forestConfig.categoricalBranchBuilder;
        numericBranchBuilder = forestConfig.numericBranchBuilder;
        samplesPerBin = forestConfig.samplesPerBin;
        binsForNumericSplits = forestConfig.binsForNumericSplits;
        buildClassificationTrees = forestConfig.buildClassificationTrees;
        degreeOfGainRatioPenalty = forestConfig.degreeOfGainRatioPenalty;
        attributeValueObservationsThreshold = forestConfig.attributeValueObservationsThreshold;
        numTrees = forestConfig.numTrees;
    }

    public void update(final Map<ForestOptions, Object> cfg) {
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
