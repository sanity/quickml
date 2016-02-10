package quickml.supervised.tree.regressionTree;

import com.google.common.collect.Lists;
import org.javatuples.Pair;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.decisionTree.branchingConditions.DTBranchingConditions;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.regressionTree.nodes.RTLeafBuilder;
import quickml.supervised.tree.regressionTree.treeBuildContexts.RTreeContextBuilder;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;
import quickml.supervised.tree.scorers.ScorerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public class RegressionTreeBuilder<I extends RegressionInstance> implements PredictiveModelBuilder< RegressionTree, I> { //why implement TreeBuilder, why not PredictiveModelBuilder
    public static final int DEFAULT_MAX_DEPTH = 5;
    public static final int DEFAULT_NUM_SAMPLES_PER_NUMERIC_BIN = 20;
    public static final IgnoreAttributesWithConstantProbability DEFAULT_ATTRIBUTE_IGNORING_STRATEGY = new IgnoreAttributesWithConstantProbability(0.7);
    public static final int DEFAULT_NUM_NUMERIC_BINS = 5;
    public static final DTBranchingConditions DEFAULT_BRANCHING_CONDITIONS = new DTBranchingConditions();
    public static final double DEFAULT_DEGREE_OF_GAIN_RATIO_PENALTY = 1.0;
    public static final double DEFAULT_IMBALANCE_PENALTY_POWER = 0.0;
    public static final double DEFAULT_MIN_SPLIT_FRACTION = 0.005;
    public static final int DEFAULT_MIN_LEAF_INSTANCES = 0;
    public static final int DEFAULT_MIN_ATTRIBUTE_OCCURENCES = 0;
    public static final LeafBuilder<MeanValueCounter> DEFAULT_LEAF_BUILDER = new RTLeafBuilder();
    public static final double DEFAULT_MIN_SCORE = 0.00000000000001;

    private final RTreeContextBuilder<I> tcb;

    private RegressionTreeBuilder(RTreeContextBuilder<I> tcb) {
        this.tcb = tcb.copy();
    }

    public RegressionTreeBuilder(){
        this.tcb = new RTreeContextBuilder<>();
    }

    @Override
    public RegressionTree buildPredictiveModel(Iterable<I> trainingData) {
        tcb.initializeConfig();
        RegressionTreeBuilderHelper<I> treeBuilderHelper = new RegressionTreeBuilderHelper<>(tcb);
        ArrayList<I> trainingDataList = Lists.newArrayList(trainingData);
        Node<MeanValueCounter> root = treeBuilderHelper.computeNodes(trainingDataList);
        return new RegressionTree(root);
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> config) {
        tcb.setConfig(config);
    }


    public synchronized RegressionTreeBuilder<I> copy() {
        return new RegressionTreeBuilder<>(tcb);
    }

    public RegressionTreeBuilder<I> maxDepth(int maxDepth) {
        tcb.maxDepth(maxDepth);
        return this;
    }
    public RegressionTreeBuilder<I> leafBuilder(LeafBuilder<MeanValueCounter> leafBuilder) {
        tcb.leafBuilder(leafBuilder);
        return this;
    }

    public RegressionTreeBuilder<I> ignoreAttributeProbability(double ignoreAttributeProbability) {
        tcb.ignoreAttributeProbability(ignoreAttributeProbability);
        return this;
    }

    public RegressionTreeBuilder<I> minSplitFraction(double minSplitFraction) {
        tcb.minSplitFraction(minSplitFraction);
        return this;
    }

    public RegressionTreeBuilder<I> minLeafInstances(int minLeafInstances) {
        tcb.minLeafInstances(minLeafInstances);
        return this;
    }

    public RegressionTreeBuilder<I> exemptAttributes(HashSet<String> exemptAttributes) {
        tcb.exemptAttributes(exemptAttributes);
        return this;
    }

    public RegressionTreeBuilder<I> attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        tcb.attributeIgnoringStrategy(attributeIgnoringStrategy);
        return this;
    }

    public RegressionTreeBuilder<I> attributeValueIgnoringStrategyBuilder(AttributeValueIgnoringStrategyBuilder<MeanValueCounter> attributeValueIgnoringStrategyBuilder) {
        tcb.attributeValueIgnoringStrategyBuilder(attributeValueIgnoringStrategyBuilder);
        return this;
    }

    public RegressionTreeBuilder<I> numSamplesPerNumericBin(int numSamplesPerNumericBin) {
        tcb.numSamplesPerNumericBin(numSamplesPerNumericBin);
        return this;
    }

    public RegressionTreeBuilder<I> numNumericBins(int numNumericBins) {
        tcb.numNumericBins(numNumericBins);
        return this;
    }

    public RegressionTreeBuilder<I> branchingConditions(DTBranchingConditions branchingConditions) {
        tcb.branchingConditions(branchingConditions);
        return this;
    }

    public RegressionTreeBuilder<I> scorerFactory(ScorerFactory<MeanValueCounter> scorerFactory) {
        tcb.scorerFactory(scorerFactory);
        return this;
    }

    public RegressionTreeBuilder<I> degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        tcb.degreeOfGainRatioPenalty(degreeOfGainRatioPenalty);
        return this;
    }

    public RegressionTreeBuilder<I> imbalancePenaltyPower(double imbalancePenaltyPower) {
        tcb.imbalancePenaltyPower(imbalancePenaltyPower);
        return this;
    }

    public RegressionTreeBuilder<I> branchFinderBuilders(ArrayList<? extends BranchFinderBuilder<MeanValueCounter>> branchFinderBuilders) {
        tcb.branchFinderBuilders(branchFinderBuilders);
        return this;
    }
    public RegressionTreeBuilder<I> minAttributeValueOccurences(int minAttributeValueOccurences) {
        tcb.minAttributeValueOccurences(minAttributeValueOccurences);
        return this;
    }

    public RegressionTreeBuilder<I> minScore(double minScore) {
        tcb.minScore(minScore);
        return this;
    }



}
