package quickml.supervised.classifier.decisionTree.tree;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public enum ForestOptions {
    SCORER(),
    MAX_DEPTH(),
    MIN_SCORE(),
    MIN_LEAF_INSTANCES(),
    NUM_TREES(),
    ATTRIBUTE_VALUE_THRESHOLD_OBSERVATIONS(),
    PENALIZE_CATEGORICAL_SPLITS(),
    ATTRIBUTE_IGNORING_STRATEGY(),
    ATTRIBUTE_VALUE_IGNORING_STRATEGY(),
    DEGREE_OF_GAIN_RATIO_PENALTY(),
    BINS_FOR_NUMERIC_SPLITS(),
    NUM_SAMPLES_PER_BIN(),
    SAMPLES_PER_BIN(),
    BRANCH_BUILDERS(),
    NUMERIC_BRANCH_BUILDER(),
    CATEGORICAL_BRANCH_BUILDER(),
    LEAF_BUILDER(),
    BOOLEAN_BRANCH_BUILDER();
}
