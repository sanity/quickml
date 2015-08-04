package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Lists;
import org.javatuples.Pair;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchingConditions.DTBranchingConditions;
import quickml.supervised.tree.decisionTree.nodes.DTLeafBuilder;
import quickml.supervised.tree.decisionTree.treeBuildContexts.DTreeContextBuilder;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.scorers.ScorerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public class DecisionTreeBuilder<I extends ClassifierInstance> implements PredictiveModelBuilder< DecisionTree, I> { //why implement TreeBuilder, why not PredictiveModelBuilder
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
    public static final LeafBuilder<ClassificationCounter> DEFAULT_LEAF_BUILDER = new DTLeafBuilder();
    public static final double DEFAULT_MIN_SCORE = 0.00000000000001;

    private final DTreeContextBuilder<I> tcb;

    private DecisionTreeBuilder(DTreeContextBuilder<I> tcb) {
        this.tcb = tcb.copy();
    }

    public DecisionTreeBuilder(){
        this.tcb = new DTreeContextBuilder<>();
    }

    @Override
    public DecisionTree buildPredictiveModel(Iterable<I> trainingData) {
        tcb.initializeConfig();
        DecisionTreeBuilderHelper<I> treeBuilderHelper = new DecisionTreeBuilderHelper<>(tcb);
        ArrayList<I> trainingDataList = Lists.newArrayList(trainingData);
        Pair<Node<ClassificationCounter>, Set<Serializable>> rootAndClassifications = treeBuilderHelper.computeNodesAndClasses(trainingDataList);
        Node<ClassificationCounter> root = rootAndClassifications.getValue0();
        Set<Serializable> classifications = rootAndClassifications.getValue1();
        return new DecisionTree(root, classifications);
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> config) {
        tcb.setConfig(config);
    }


    public synchronized DecisionTreeBuilder<I> copy() {
        return new DecisionTreeBuilder<>(tcb);
    }

    public DecisionTreeBuilder<I> maxDepth(int maxDepth) {
        tcb.maxDepth(maxDepth);
        return this;
    }
    public DecisionTreeBuilder<I> leafBuilder(LeafBuilder<ClassificationCounter> leafBuilder) {
        tcb.leafBuilder(leafBuilder);
        return this;
    }

    public DecisionTreeBuilder<I> ignoreAttributeProbability(double ignoreAttributeProbability) {
        tcb.ignoreAttributeProbability(ignoreAttributeProbability);
        return this;
    }

    public DecisionTreeBuilder<I> minSplitFraction(double minSplitFraction) {
        tcb.minSplitFraction(minSplitFraction);
        return this;
    }

    public DecisionTreeBuilder<I> minLeafInstances(int minLeafInstances) {
        tcb.minLeafInstances(minLeafInstances);
        return this;
    }

    public DecisionTreeBuilder<I> exemptAttributes(HashSet<String> exemptAttributes) {
        tcb.exemptAttributes(exemptAttributes);
        return this;
    }

    public DecisionTreeBuilder<I> attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        tcb.attributeIgnoringStrategy(attributeIgnoringStrategy);
        return this;
    }

    public DecisionTreeBuilder<I> attributeValueIgnoringStrategyBuilder(AttributeValueIgnoringStrategyBuilder<ClassificationCounter> attributeValueIgnoringStrategyBuilder) {
        tcb.attributeValueIgnoringStrategyBuilder(attributeValueIgnoringStrategyBuilder);
        return this;
    }

    public DecisionTreeBuilder<I> numSamplesPerNumericBin(int numSamplesPerNumericBin) {
        tcb.numSamplesPerNumericBin(numSamplesPerNumericBin);
        return this;
    }

    public DecisionTreeBuilder<I> numNumericBins(int numNumericBins) {
        tcb.numNumericBins(numNumericBins);
        return this;
    }

    public DecisionTreeBuilder<I> branchingConditions(DTBranchingConditions branchingConditions) {
        tcb.branchingConditions(branchingConditions);
        return this;
    }

    public DecisionTreeBuilder<I> scorerFactory(ScorerFactory<ClassificationCounter> scorerFactory) {
        tcb.scorerFactory(scorerFactory);
        return this;
    }

    public DecisionTreeBuilder<I> degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        tcb.degreeOfGainRatioPenalty(degreeOfGainRatioPenalty);
        return this;
    }

    public DecisionTreeBuilder<I> imbalancePenaltyPower(double imbalancePenaltyPower) {
        tcb.imbalancePenaltyPower(imbalancePenaltyPower);
        return this;
    }

    public DecisionTreeBuilder<I> branchFinderBuilders(ArrayList<? extends BranchFinderBuilder<ClassificationCounter>> branchFinderBuilders) {
        tcb.branchFinderBuilders(branchFinderBuilders);
        return this;
    }
    public DecisionTreeBuilder<I> minAttributeValueOccurences(int minAttributeValueOccurences) {
        tcb.minAttributeValueOccurences(minAttributeValueOccurences);
        return this;
    }

    public DecisionTreeBuilder<I> minScore(double minScore) {
        tcb.minScore(minScore);
        return this;
    }



}
