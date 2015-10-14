package quickml.supervised.tree.decisionTree.treeBuildContexts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.BranchFinderAndReducerFactory;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;

import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.constants.AttributeType;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTBinaryCatBranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTCatBranchFinderBuilder;
import quickml.supervised.tree.dataProcessing.BasicTrainingDataSurveyor;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTNumBranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchingConditions.DTBranchingConditions;
import quickml.supervised.tree.decisionTree.reducers.reducerFactories.DTBinaryCatBranchReducerFactory;
import quickml.supervised.tree.decisionTree.reducers.reducerFactories.DTCatBranchReducerFactory;
import quickml.supervised.tree.decisionTree.reducers.reducerFactories.DTNumBranchReducerFactory;
import quickml.supervised.tree.decisionTree.scorers.PenalizedGiniImpurityScorerFactory;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounterProducer;
import quickml.supervised.tree.nodes.LeafBuilder;
import quickml.supervised.tree.reducers.ReducerFactory;
import quickml.supervised.tree.scorers.ScorerFactory;
import quickml.supervised.tree.treeBuildContexts.TreeContextBuilder;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.io.Serializable;
import java.util.*;

import static quickml.supervised.tree.decisionTree.DecisionTreeBuilder.*;

import static quickml.supervised.tree.constants.ForestOptions.*;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public class DTreeContextBuilder<I extends ClassifierInstance> extends TreeContextBuilder<I, ClassificationCounter> {

    @Override
    public DTreeContextBuilder<I> createTreeBuildContext() {
        return new DTreeContextBuilder<>();
    }

    @Override
    public DTreeContext<I> buildContext(List<I> trainingData) {
        boolean considerBooleanAttributes = hasBranchFinderBuilder(BranchType.BOOLEAN);
        BasicTrainingDataSurveyor<I> decTreeTrainingDataSurveyor = new BasicTrainingDataSurveyor<>(considerBooleanAttributes);
        Map<AttributeType, Set<String>> candidateAttributesByType = decTreeTrainingDataSurveyor.groupAttributesByType(trainingData);
        ClassificationCounter classificationCounts = getValueCounterProducer().getValueCounter(trainingData);
        List<BranchFinderAndReducerFactory<I, ClassificationCounter>> branchFinderAndReducers = intializeBranchFindersAndReducers(classificationCounts, candidateAttributesByType);
        return new DTreeContext<I>(classificationCounts.allClassifications(),
                (BranchingConditions<ClassificationCounter>)config.get(BRANCHING_CONDITIONS.name()),
                (ScorerFactory<ClassificationCounter>)config.get(SCORER_FACTORY.name()),
                branchFinderAndReducers,
                (LeafBuilder<ClassificationCounter>)config.get(LEAF_BUILDER.name()),
                getValueCounterProducer());
    }

    @Override
    public ClassificationCounterProducer<I> getValueCounterProducer() {
        return new ClassificationCounterProducer<>();
    }

    @Override
    public synchronized DTreeContextBuilder<I> copy() {
        //TODO: should only copy the config, and make sure the others get updated.  This is redundant.
        DTreeContextBuilder<I> copy = createTreeBuildContext();
        copy.config = deepCopyConfig(this.config);
        return copy;
    }

    private ArrayList<BranchFinderBuilder<ClassificationCounter>> copyBranchFinderBuilders(Map<String, Serializable> config) {
        ArrayList<BranchFinderBuilder<ClassificationCounter>> copiedBranchFinderBuilders = Lists.newArrayList();
        if (config.containsKey(BRANCH_FINDER_BUILDERS.name())) {
            List<BranchFinderBuilder<ClassificationCounter>> bfbs = (List<BranchFinderBuilder<ClassificationCounter>>) config.get(BRANCH_FINDER_BUILDERS.name());
            if (bfbs != null && !bfbs.isEmpty()) {
                for (BranchFinderBuilder<ClassificationCounter> branchFinderBuilder : bfbs) {
                    copiedBranchFinderBuilders.add(branchFinderBuilder.copy());
                }
                return copiedBranchFinderBuilders;
            } else {
                return getDefaultBranchFinderBuilders();
            }
        }
        return getDefaultBranchFinderBuilders();
    }

    private List<BranchFinderAndReducerFactory<I, ClassificationCounter>> intializeBranchFindersAndReducers(ClassificationCounter classificationCounts, Map<AttributeType, Set<String>> candidateAttributesByType) {
        /**Branch finders should be paired with the correct reducers. With this method, we don't leave open the possibility for a user to make a mistake with the pairings.
         * */
        //
        List<BranchFinderAndReducerFactory<I, ClassificationCounter>> branchFindersAndReducers = Lists.newArrayList();
        int numClasses = classificationCounts.allClassifications().size();
        Serializable minorityClassification = ClassificationCounter.getLeastPopularClass(classificationCounts);
        Map<BranchType, ReducerFactory<I, ClassificationCounter>> reducerMap = getDefaultReducerFactories(minorityClassification);
        for (BranchFinderBuilder<ClassificationCounter> branchFinderBuilder : getBranchFinderBuilders()) {
            if (useBranchFinder(branchFinderBuilder, numClasses)) {
                AttributeType attributeType = AttributeType.convertBranchTypeToAttributeType(branchFinderBuilder.getBranchType());
                BranchFinder<ClassificationCounter> branchFinder = branchFinderBuilder.buildBranchFinder(classificationCounts, candidateAttributesByType.get(attributeType));
                ReducerFactory<I, ClassificationCounter> reducerFactory = reducerMap.get(branchFinderBuilder.getBranchType());
                reducerFactory.updateBuilderConfig(config);
                branchFindersAndReducers.add(new BranchFinderAndReducerFactory<I, ClassificationCounter>(branchFinder, reducerFactory));
            }
        }
        return branchFindersAndReducers;
    }


    private boolean useBranchFinder(BranchFinderBuilder<ClassificationCounter> branchFinderBuilder, int numClasses) {
        if (branchFinderBuilder.getBranchType().equals(BranchType.BINARY_CATEGORICAL) && numClasses != 2) {
            return false;
        }
        if (branchFinderBuilder.getBranchType().equals(BranchType.CATEGORICAL) && numClasses == 2) {
            return false;
        }
        return true;
    }

    public static <I extends ClassifierInstance> Map<BranchType, ReducerFactory<I, ClassificationCounter>>  getDefaultReducerFactories(Serializable minorityClassification) {
        Map<BranchType,  ReducerFactory<I, ClassificationCounter>> reducerFactories = Maps.newHashMap();
        reducerFactories.put(BranchType.BINARY_CATEGORICAL, new DTBinaryCatBranchReducerFactory<I>(minorityClassification));
        reducerFactories.put(BranchType.CATEGORICAL, new DTCatBranchReducerFactory<I>());
        reducerFactories.put(BranchType.NUMERIC, new DTNumBranchReducerFactory<I>());
        reducerFactories.put(BranchType.BOOLEAN, new DTCatBranchReducerFactory<I>());
        return reducerFactories;
    }

    public static <I extends ClassifierInstance> ArrayList<BranchFinderBuilder<ClassificationCounter>> getDefaultBranchFinderBuilders() {
        ArrayList<BranchFinderBuilder<ClassificationCounter>> branchFinderBuilders = Lists.newArrayList();
        branchFinderBuilders.add(new DTBinaryCatBranchFinderBuilder());
        branchFinderBuilders.add(new DTCatBranchFinderBuilder());
        branchFinderBuilders.add(new DTNumBranchFinderBuilder());
        return branchFinderBuilders;
    }

    @Override
    public void setDefaultsAsNeeded() {
        if (!config.containsKey(BRANCH_FINDER_BUILDERS.name())) {
            branchFinderBuilders(DTreeContextBuilder.getDefaultBranchFinderBuilders());
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

    private ScorerFactory<ClassificationCounter> getDefaultScorerFactory(){
        return new PenalizedGiniImpurityScorerFactory(DEFAULT_DEGREE_OF_GAIN_RATIO_PENALTY, DEFAULT_IMBALANCE_PENALTY_POWER);
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
            copiedConfig.put(ATTRIBUTE_IGNORING_STRATEGY.name(), ((AttributeIgnoringStrategy)config.get(ATTRIBUTE_IGNORING_STRATEGY.name())).copy());
        }
        if (config.containsKey(NUM_SAMPLES_PER_NUMERIC_BIN.name())) {
            copiedConfig.put(NUM_SAMPLES_PER_NUMERIC_BIN.name(), config.get(NUM_SAMPLES_PER_NUMERIC_BIN.name()));
        }
        if (config.containsKey(NUM_NUMERIC_BINS.name())) {
            copiedConfig.put(NUM_NUMERIC_BINS.name(), config.get(NUM_NUMERIC_BINS.name()));
        }
        if (config.containsKey(BRANCHING_CONDITIONS.name())) {
            copiedConfig.put(BRANCHING_CONDITIONS.name(), ((BranchingConditions<ClassificationCounter>)config.get(BRANCHING_CONDITIONS.name())).copy());
        }
        if (config.containsKey(SCORER_FACTORY.name())) {
            copiedConfig.put(SCORER_FACTORY.name(), ((ScorerFactory<ClassificationCounter>)config.get(SCORER_FACTORY.name())).copy());
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
            copiedConfig.put(LEAF_BUILDER.name(), ((LeafBuilder<ClassificationCounter>)config.get(LEAF_BUILDER.name())).copy());
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
            copiedConfig.put(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name(), ((AttributeValueIgnoringStrategyBuilder<ClassificationCounter>) config.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY.name())).copy());
        }
        return copiedConfig;
    }

    public void maxDepth(int maxDepth) {
        config.put(MAX_DEPTH.name(), maxDepth);
    }

    public void leafBuilder(LeafBuilder<ClassificationCounter> leafBuilder) {
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
    public void attributeValueIgnoringStrategyBuilder(AttributeValueIgnoringStrategyBuilder<ClassificationCounter> attributeValueIgnoringStrategyBuilder) {
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

    public void scorerFactory(ScorerFactory<ClassificationCounter> scorerFactory) {
        config.put(SCORER_FACTORY.name(), scorerFactory);
    }

    public void degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        config.put(DEGREE_OF_GAIN_RATIO_PENALTY.name(), degreeOfGainRatioPenalty);
    }

    public void imbalancePenaltyPower(double imbalancePenaltyPower) {
        config.put(IMBALANCE_PENALTY_POWER.name(), imbalancePenaltyPower);
    }

    public void branchFinderBuilders(ArrayList<? extends BranchFinderBuilder<ClassificationCounter>> branchFinderBuilders) {
        config.put(BRANCH_FINDER_BUILDERS.name(), branchFinderBuilders);
    }

    public void minAttributeValueOccurences(int minAttributeValueOccurences) {
        config.put(MIN_ATTRIBUTE_VALUE_OCCURRENCES.name(), minAttributeValueOccurences);
    }
    public void minScore(double minScore) {
        config.put(MIN_SCORE.name(), minScore);
    }

}
