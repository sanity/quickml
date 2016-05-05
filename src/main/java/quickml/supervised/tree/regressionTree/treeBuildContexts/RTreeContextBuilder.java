package quickml.supervised.tree.regressionTree.treeBuildContexts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducerFactory;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.constants.AttributeType;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.dataProcessing.BasicTrainingDataSurveyor;
import quickml.supervised.tree.decisionTree.branchingConditions.DTBranchingConditions;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.reducers.ReducerFactory;
import quickml.supervised.tree.regressionTree.branchFinders.branchFinderBuilders.RTCatBranchFinderBuilder;
import quickml.supervised.tree.regressionTree.branchFinders.branchFinderBuilders.RTNumBranchFinderBuilder;
import quickml.supervised.tree.regressionTree.reducers.reducerFactories.RTCatBranchReducerFactory;
import quickml.supervised.tree.regressionTree.reducers.reducerFactories.RTNumBranchReducerFactory;
import quickml.supervised.tree.regressionTree.scorers.RTPenalizedMSEScorerFactory;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounterProducer;
import quickml.supervised.tree.scorers.ScorerFactory;
import quickml.supervised.tree.treeBuildContexts.TreeContextBuilder;

import java.io.Serializable;
import java.util.*;

import static quickml.supervised.tree.constants.ForestOptions.*;
import static quickml.supervised.tree.regressionTree.RegressionTreeBuilder.*;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public class RTreeContextBuilder<I extends RegressionInstance> extends TreeContextBuilder<I, MeanValueCounter> {

    @Override
    public RTreeContextBuilder<I> createTreeBuildContext() {
        return new RTreeContextBuilder<>();
    }

    @Override
    public RTreeContext<I> buildContext(List<I> trainingData) {
        boolean considerBooleanAttributes = hasBranchFinderBuilder(BranchType.BOOLEAN);
        BasicTrainingDataSurveyor<I> decTreeTrainingDataSurveyor = new BasicTrainingDataSurveyor<>(considerBooleanAttributes);
        Map<AttributeType, Set<String>> candidateAttributesByType = decTreeTrainingDataSurveyor.groupAttributesByType(trainingData);
        List<BranchFinderAndReducerFactory<I, MeanValueCounter>> branchFinderAndReducers = intializeBranchFindersAndReducers(candidateAttributesByType);
        return new RTreeContext<I>(
                (BranchingConditions<MeanValueCounter>) config.get(BRANCHING_CONDITIONS.name()),
                (ScorerFactory<MeanValueCounter>) config.get(SCORER_FACTORY.name()),
                branchFinderAndReducers,
                (LeafBuilder<MeanValueCounter>) config.get(LEAF_BUILDER.name()),
                getValueCounterProducer());
    }

    @Override
    public MeanValueCounterProducer<I> getValueCounterProducer() {
        return new MeanValueCounterProducer<>();
    }

    @Override
    public synchronized RTreeContextBuilder<I> copy() {
        //TODO: should only copy the config, and make sure the others get updated.  This is redundant.
        RTreeContextBuilder<I> copy = createTreeBuildContext();
        copy.config = deepCopyConfig(this.config);
        return copy;
    }

    private ArrayList<BranchFinderBuilder<MeanValueCounter>> copyBranchFinderBuilders(Map<String, Serializable> config) {
        ArrayList<BranchFinderBuilder<MeanValueCounter>> copiedBranchFinderBuilders = Lists.newArrayList();
        if (config.containsKey(BRANCH_FINDER_BUILDERS.name())) {
            List<BranchFinderBuilder<MeanValueCounter>> bfbs = (List<BranchFinderBuilder<MeanValueCounter>>) config.get(BRANCH_FINDER_BUILDERS.name());
            if (bfbs != null && !bfbs.isEmpty()) {
                for (BranchFinderBuilder<MeanValueCounter> branchFinderBuilder : bfbs) {
                    copiedBranchFinderBuilders.add(branchFinderBuilder.copy());
                }
                return copiedBranchFinderBuilders;
            } else {
                return getDefaultBranchFinderBuilders();
            }
        }
        return getDefaultBranchFinderBuilders();
    }

    private List<BranchFinderAndReducerFactory<I, MeanValueCounter>> intializeBranchFindersAndReducers(Map<AttributeType, Set<String>> candidateAttributesByType) {
        /**Branch finders should be paired with the correct reducers. With this method, we don't leave open the possibility for a user to make a mistake with the pairings.
         * */
        //
        List<BranchFinderAndReducerFactory<I, MeanValueCounter>> branchFindersAndReducers = Lists.newArrayList();
        Map<BranchType, ReducerFactory<I, MeanValueCounter>> reducerMap = getDefaultReducerFactories();
        for (BranchFinderBuilder<MeanValueCounter> branchFinderBuilder : getBranchFinderBuilders()) {
                AttributeType attributeType = AttributeType.convertBranchTypeToAttributeType(branchFinderBuilder.getBranchType());
                BranchFinder<MeanValueCounter> branchFinder = branchFinderBuilder.buildBranchFinder(null, candidateAttributesByType.get(attributeType));
                ReducerFactory<I, MeanValueCounter> reducerFactory = reducerMap.get(branchFinderBuilder.getBranchType());
                reducerFactory.updateBuilderConfig(config);
                branchFindersAndReducers.add(new BranchFinderAndReducerFactory<I, MeanValueCounter>(branchFinder, reducerFactory));
        }
        return branchFindersAndReducers;
    }

    public static <I extends RegressionInstance> Map<BranchType, ReducerFactory<I, MeanValueCounter>> getDefaultReducerFactories() {
        Map<BranchType, ReducerFactory<I, MeanValueCounter>> reducerFactories = Maps.newHashMap();
        reducerFactories.put(BranchType.RT_CATEGORICAL, new RTCatBranchReducerFactory<I>());
        reducerFactories.put(BranchType.RT_NUMERIC, new RTNumBranchReducerFactory<I>());
        return reducerFactories;
    }

    public static <I extends RegressionInstance> ArrayList<BranchFinderBuilder<MeanValueCounter>> getDefaultBranchFinderBuilders() {
        ArrayList<BranchFinderBuilder<MeanValueCounter>> branchFinderBuilders = Lists.newArrayList();
        branchFinderBuilders.add(new RTCatBranchFinderBuilder());
        branchFinderBuilders.add(new RTCatBranchFinderBuilder());
        branchFinderBuilders.add(new RTNumBranchFinderBuilder());
        return branchFinderBuilders;
    }

    @Override
    public void setDefaultsAsNeeded() {
        if (!config.containsKey(BRANCH_FINDER_BUILDERS.name())) {
            branchFinderBuilders(RTreeContextBuilder.getDefaultBranchFinderBuilders());
        }
        if (!config.containsKey(MAX_DEPTH.name())) {
            maxDepth(DEFAULT_MAX_DEPTH);
        }
        if (!config.containsKey(MIN_SLPIT_FRACTION.name())) {
            minSplitFraction(DEFAULT_MIN_SPLIT_FRACTION);
        }
        if (!config.containsKey(ATTRIBUTE_IGNORING_STRATEGY.name())) {
            attributeIgnoringStrategy(DEFAULT_ATTRIBUTE_IGNORING_STRATEGY);
        }
        if (!config.containsKey(NUM_SAMPLES_PER_NUMERIC_BIN.name())) {
            numSamplesPerNumericBin(DEFAULT_NUM_SAMPLES_PER_NUMERIC_BIN);
        }
        if (!config.containsKey(NUM_NUMERIC_BINS.name())) {
            numNumericBins(DEFAULT_NUM_NUMERIC_BINS);
        }
        if (!config.containsKey(BRANCHING_CONDITIONS.name())) {
            branchingConditions(DEFAULT_BRANCHING_CONDITIONS);
        }
        if (!config.containsKey(SCORER_FACTORY.name())) {
            scorerFactory(getDefaultScorerFactory());
        }
        if (!config.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY.name())) {
            degreeOfGainRatioPenalty(DEFAULT_DEGREE_OF_GAIN_RATIO_PENALTY);
        }
        if (!config.containsKey(IMBALANCE_PENALTY_POWER.name())) {
            imbalancePenaltyPower(DEFAULT_IMBALANCE_PENALTY_POWER);
        }
        if (!config.containsKey(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name())) {
            minAttributeValueOccurences(DEFAULT_MIN_ATTRIBUTE_OCCURENCES);
        }
        if (!config.containsKey(LEAF_BUILDER.name())) {
            leafBuilder(DEFAULT_LEAF_BUILDER);
        }

        if (!config.containsKey(MIN_LEAF_INSTANCES.name())) {
            minLeafInstances(DEFAULT_MIN_LEAF_INSTANCES);
        }
        if (!config.containsKey(MIN_SCORE.name())) {
            minScore(DEFAULT_MIN_SCORE);
        }
    }

    private ScorerFactory<MeanValueCounter> getDefaultScorerFactory() {
        return new RTPenalizedMSEScorerFactory(DEFAULT_DEGREE_OF_GAIN_RATIO_PENALTY, DEFAULT_IMBALANCE_PENALTY_POWER);
    }


    @Override
    public synchronized Map<String, Serializable> deepCopyConfig(Map<String, Serializable> config) {
        Map<String, Serializable> copiedConfig = Maps.newHashMap();
        if (config.containsKey(BRANCH_FINDER_BUILDERS.name())) {
            copiedConfig.put(BRANCH_FINDER_BUILDERS.name(), copyBranchFinderBuilders(config));
        }
        if (config.containsKey(MAX_DEPTH.name())) {
            copiedConfig.put(MAX_DEPTH.name(), config.get(MAX_DEPTH.name()));
        }
        if (config.containsKey(MIN_SLPIT_FRACTION.name())) {
            copiedConfig.put(MIN_SLPIT_FRACTION.name(), config.get(MIN_SLPIT_FRACTION.name()));
        }
        if (config.containsKey(ATTRIBUTE_IGNORING_STRATEGY.name())) {
            copiedConfig.put(ATTRIBUTE_IGNORING_STRATEGY.name(), ((AttributeIgnoringStrategy) config.get(ATTRIBUTE_IGNORING_STRATEGY.name())).copy());
        }
        if (config.containsKey(NUM_SAMPLES_PER_NUMERIC_BIN.name())) {
            copiedConfig.put(NUM_SAMPLES_PER_NUMERIC_BIN.name(), config.get(NUM_SAMPLES_PER_NUMERIC_BIN.name()));
        }
        if (config.containsKey(NUM_NUMERIC_BINS.name())) {
            copiedConfig.put(NUM_NUMERIC_BINS.name(), config.get(NUM_NUMERIC_BINS.name()));
        }
        if (config.containsKey(BRANCHING_CONDITIONS.name())) {
            copiedConfig.put(BRANCHING_CONDITIONS.name(), ((BranchingConditions<MeanValueCounter>) config.get(BRANCHING_CONDITIONS.name())).copy());
        }
        if (config.containsKey(SCORER_FACTORY.name())) {
            copiedConfig.put(SCORER_FACTORY.name(), ((ScorerFactory<MeanValueCounter>) config.get(SCORER_FACTORY.name())).copy());
        }
        if (config.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY.name())) {
            copiedConfig.put(DEGREE_OF_GAIN_RATIO_PENALTY.name(), config.get(DEGREE_OF_GAIN_RATIO_PENALTY.name()));
        }
        if (config.containsKey(IMBALANCE_PENALTY_POWER.name())) {
            copiedConfig.put(IMBALANCE_PENALTY_POWER.name(), config.get(IMBALANCE_PENALTY_POWER.name()));
        }
        if (config.containsKey(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name())) {
            copiedConfig.put(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name(), config.get(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name()));
        }
        if (config.containsKey(LEAF_BUILDER.name())) {
            copiedConfig.put(LEAF_BUILDER.name(), ((LeafBuilder<MeanValueCounter>) config.get(LEAF_BUILDER.name())).copy());
        }

        if (config.containsKey(MIN_LEAF_INSTANCES.name())) {
            copiedConfig.put(MIN_LEAF_INSTANCES.name(), config.get(MIN_LEAF_INSTANCES.name()));
        }

        if (config.containsKey(MIN_SCORE.name())) {
            copiedConfig.put(MIN_SCORE.name(), config.get(MIN_SCORE.name()));
        }

        if (config.containsKey(EXEMPT_ATTRIBUTES.name())) {
            copiedConfig.put(EXEMPT_ATTRIBUTES.name(), Sets.newHashSet(((Set<String>) config.get(EXEMPT_ATTRIBUTES.name()))));
        }

        if (config.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name())) {
            copiedConfig.put(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name(), ((AttributeValueIgnoringStrategyBuilder<MeanValueCounter>) config.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY.name())).copy());
        }
        return copiedConfig;
    }

    public void maxDepth(int maxDepth) {
        config.put(MAX_DEPTH.name(), maxDepth);
    }

    public void leafBuilder(LeafBuilder<MeanValueCounter> leafBuilder) {
        config.put(LEAF_BUILDER.name(), leafBuilder);
    }

    //doesn't have default
    public void ignoreAttributeProbability(double ignoreAttributeProbability) {
        config.put(ATTRIBUTE_IGNORING_STRATEGY.name(), new IgnoreAttributesWithConstantProbability(ignoreAttributeProbability));
    }

    public void minSplitFraction(double minSplitFraction) {
        config.put(MIN_SLPIT_FRACTION.name(), minSplitFraction);
    }

    public void minLeafInstances(int minLeafInstances) {
        config.put(MIN_LEAF_INSTANCES.name(), minLeafInstances);
    }

    //doesn't have a default.
    public void exemptAttributes(HashSet<String> exemptAttributes) {
        config.put(EXEMPT_ATTRIBUTES.name(), exemptAttributes);
    }

    public void attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        config.put(ATTRIBUTE_IGNORING_STRATEGY.name(), attributeIgnoringStrategy);
    }

    //if not specified, the appropriate attrValIgnoringStrategy will be chosen when building BranchFinders
    public void attributeValueIgnoringStrategyBuilder(AttributeValueIgnoringStrategyBuilder<MeanValueCounter> attributeValueIgnoringStrategyBuilder) {
        config.put(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name(), attributeValueIgnoringStrategyBuilder);
    }

    public void numSamplesPerNumericBin(int numSamplesPerNumericBin) {
        config.put(NUM_SAMPLES_PER_NUMERIC_BIN.name(), numSamplesPerNumericBin);
    }

    public void numNumericBins(int numNumericBins) {
        config.put(NUM_NUMERIC_BINS.name(), numNumericBins);
    }

    public void branchingConditions(DTBranchingConditions branchingConditions) {
        config.put(BRANCHING_CONDITIONS.name(), branchingConditions);
    }

    public void scorerFactory(ScorerFactory<MeanValueCounter> scorerFactory) {
        config.put(SCORER_FACTORY.name(), scorerFactory);
    }

    public void degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY.name(), degreeOfGainRatioPenalty);
    }

    public void imbalancePenaltyPower(double imbalancePenaltyPower) {
        config.put(IMBALANCE_PENALTY_POWER.name(), imbalancePenaltyPower);
    }

    public void branchFinderBuilders(ArrayList<? extends BranchFinderBuilder<MeanValueCounter>> branchFinderBuilders) {
        config.put(BRANCH_FINDER_BUILDERS.name(), branchFinderBuilders);
    }

    public void minAttributeValueOccurences(int minAttributeValueOccurences) {
        config.put(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name(), minAttributeValueOccurences);
    }

    public void minScore(double minScore) {
        config.put(MIN_SCORE.name(), minScore);
    }

}
