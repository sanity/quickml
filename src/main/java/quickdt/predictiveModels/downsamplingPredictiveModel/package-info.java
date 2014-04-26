/**
 * A predictive model wrapper and related classes that can be used to improve performance on highly
 * <a href="https://archive.nyu.edu/bitstream/2451/27763/2/CPP-02-00.pdf">imbalanced</a> datasets
 * with two possible classifications (a majority classification and a minority classification).
 * It works by reducing the imbalance by throwing away, at random, a proportion of the instances
 * with the majority classification.  It then statistically corrects for this at prediction time.
 */
package quickdt.predictiveModels.downsamplingPredictiveModel;