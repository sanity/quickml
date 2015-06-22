package quickml.supervised.tree.decisionTree.treeBuildContexts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategyBuilder;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import static quickml.supervised.tree.constants.ForestOptions.*;

import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTBinaryCatBranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTBranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTCatBranchFinderBuilder;
import quickml.supervised.tree.completeDataSetSummaries.DTreeTrainingDataSurveyor;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTNumBranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchingConditions.DTBranchingConditions;
import quickml.supervised.tree.decisionTree.reducers.*;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounterProducer;
import quickml.supervised.tree.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.tree.scorers.Scorer;
import quickml.supervised.tree.treeBuildContexts.TreeContextBuilder;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.decisionTree.nodes.DTNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 6/20/15.
 */
public class DTreeContextBuilder<I extends ClassifierInstance> extends TreeContextBuilder<I, ClassificationCounter, DTNode> {

    public static final int DEFAULT_MAX_DEPTH = 7;
    public static final int DEFAULT_NUM_SAMPLES_PER_NUMERIC_BIN = 50;
    public static final IgnoreAttributesWithConstantProbability DEFAULT_ATTRIBUTE_IGNORING_STRATEGY = new IgnoreAttributesWithConstantProbability(0.7);
    public static final int DEFAULT_NUM_NUMERIC_BINS = 6;
    public static final GiniImpurityScorer DEFAULT_SCORER = new GiniImpurityScorer();
    public static final DTBranchingConditions DEFAULT_BRANCHING_CONDITIONS = new DTBranchingConditions();
    public static final double DEFAULT_DEGREE_OF_GAIN_RATIO_PENALTY = 1.0;
    public static final double DEFAULT_IMBALANCE_PENALTY_POWER = 0.0;
    public static final double DEFAULT_MIN_SPLIT_FRACTION = 0.01;
    private Map<String, Object> cfg = Maps.newHashMap();

    //check that haven't missed any settings.
    public DTreeContextBuilder<I> maxDepth(int maxDepth) {
        cfg.put(MAX_DEPTH.name(), maxDepth);
        return this;
    }

    public DTreeContextBuilder<I> ignoreAttributeProbability(int ignoreAttributeProbability) {
        cfg.put(ATTRIBUTE_IGNORING_STRATEGY.name(), new IgnoreAttributesWithConstantProbability(ignoreAttributeProbability));
        return this;
    }

    public DTreeContextBuilder<I> minSplitFraction(double minSplitFraction) {
        cfg.put(MIN_SLPIT_FRACTION.name(), minSplitFraction);
        return this;
    }

    public DTreeContextBuilder<I> exemptAttributes(Set<String> exemptAttributes) {
        cfg.put(EXEMPT_ATTRIBUTES.name(), exemptAttributes);
        return this;
    }

    public DTreeContextBuilder<I> attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        cfg.put(ATTRIBUTE_IGNORING_STRATEGY.name(), attributeIgnoringStrategy);
        return this;
    }

    public DTreeContextBuilder<I> attributeValueIgnoringStrategyBuilder(AttributeValueIgnoringStrategyBuilder<ClassificationCounter> attributeValueIgnoringStrategyBuilder) {
        cfg.put(ATTRIBUTE_VALUE_IGNORING_STRATEGY_BUILDER.name(), attributeValueIgnoringStrategyBuilder);
        return this;
    }

    public DTreeContextBuilder<I> numSamplesPerNumericBin(int numSamplesPerNumericBin) {
        cfg.put(NUM_SAMPLES_PER_NUMERIC_BIN.name(), numSamplesPerNumericBin);
        return this;
    }

    public DTreeContextBuilder<I> numNumericBins(int numNumericBins) {
        cfg.put(NUM_NUMERIC_BINS.name(), numNumericBins);
        return this;
    }

    public DTreeContextBuilder<I> branchingConditions(DTBranchingConditions branchingConditions) {
        cfg.put(BRANCHING_CONDITIONS.name(), branchingConditions);
        return this;
    }

    public DTreeContextBuilder<I> scorer(Scorer<ClassificationCounter> scorer) {
        cfg.put(SCORER.name(), scorer);
        return this;
    }

    public DTreeContextBuilder<I> degreeOfGainRatioPenalty(double degreeOfGainRatioPenalty) {
        cfg.put(DEGREE_OF_GAIN_RATIO_PENALTY.name(), degreeOfGainRatioPenalty);
        return this;
    }

    public DTreeContextBuilder<I> imbalancePenaltyPower(double imbalancePenaltyPower) {
        cfg.put(IMBALANCE_PENALTY_POWER.name(), imbalancePenaltyPower);
        return this;
    }

    public DTreeContextBuilder<I> branchFinderBuilders(List<? extends BranchFinderBuilder<ClassificationCounter, DTNode>> branchFinderBuilders) {
        cfg.put(BRANCH_FINDER_BUILDERS.name(), branchFinderBuilders);
        return this;
    }

    @Override
    public DTreeContextBuilder<I> createTreeBuildContext() {
        return new DTreeContextBuilder<>();
    }

    @Override
    public DTreeContext<I> buildContext(List<I> trainingData) {
        setDefaults();
        boolean considerBooleanAttributes = hasBranchFinderBuilder(BranchType.BOOLEAN);
        DTreeTrainingDataSurveyor<I> decTreeTrainingDataSurveyor = new DTreeTrainingDataSurveyor<>(considerBooleanAttributes);
        Map<BranchType, Set<String>> candidateAttributesByBranchType = decTreeTrainingDataSurveyor.groupAttributesByType(trainingData);
        ClassificationCounter classificationCounts= getValueCounterProducer().getValueCounter(trainingData);
        List<DTreeBranchFinderAndReducer<I>> branchFinderAndReducers = intializeBranchFindersAndReducers(classificationCounts, candidateAttributesByBranchType);
        updateAll(branchFinderAndReducers);
        return new DTreeContext<I>(classificationCounts.allClassifications(), branchingConditions, scorer, branchFinderAndReducers, leafBuilder, getValueCounterProducer());
    }

    @Override
    public ClassificationCounterProducer<I> getValueCounterProducer() {
        return new ClassificationCounterProducer<>();
    }

    @Override
    public DTreeContextBuilder<I> copy() {
        DTreeContextBuilder<I> copy = createTreeBuildContext();
        List<BranchFinderBuilder<ClassificationCounter, DTNode>> copiedBranchFinderBuilders = Lists.newArrayList();
        for (BranchFinderBuilder<ClassificationCounter, DTNode> branchFinderBuilder : this.branchFinderBuilders) {
            copiedBranchFinderBuilders.add(branchFinderBuilder.copy());
        }
        copy.branchFinderBuilders = copiedBranchFinderBuilders;
        copy.branchingConditions = branchingConditions.copy();
        copy.scorer = scorer.copy();
        copy.leafBuilder = leafBuilder;
        return copy;
    }


    private void setDefaults() {
        if (!cfg.containsKey(BRANCH_FINDER_BUILDERS.name())) {
            branchFinderBuilders(getDefaultBranchFinderBuilders());
        }
        if (!cfg.containsKey(MAX_DEPTH.name())){
            maxDepth(DEFAULT_MAX_DEPTH);
        }
        if (!cfg.containsKey(MIN_SLPIT_FRACTION.name())){
            minSplitFraction(DEFAULT_MIN_SPLIT_FRACTION);
        }
        if (!cfg.containsKey(ATTRIBUTE_IGNORING_STRATEGY.name())){
            attributeIgnoringStrategy(DEFAULT_ATTRIBUTE_IGNORING_STRATEGY);
        }
        if (!cfg.containsKey(NUM_SAMPLES_PER_NUMERIC_BIN.name())){
            numSamplesPerNumericBin(DEFAULT_NUM_SAMPLES_PER_NUMERIC_BIN);
        }
        if (!cfg.containsKey(NUM_NUMERIC_BINS.name())){
            numNumericBins(DEFAULT_NUM_NUMERIC_BINS);
        }
        if (!cfg.containsKey(BRANCHING_CONDITIONS.name())){
            branchingConditions(DEFAULT_BRANCHING_CONDITIONS);
        }
        if (!cfg.containsKey(SCORER.name())) {
            scorer(DEFAULT_SCORER);
        }
        if (!cfg.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY.name())){
            degreeOfGainRatioPenalty(DEFAULT_DEGREE_OF_GAIN_RATIO_PENALTY);
        }
        if (!cfg.containsKey(IMBALANCE_PENALTY_POWER.name())){
            imbalancePenaltyPower(DEFAULT_IMBALANCE_PENALTY_POWER);
        }
    }

    private void updateAll(List<DTreeBranchFinderAndReducer<I>> branchFinderAndReducers) {
        //TODO: consider making reducers exist is fields in the parent class.
        for (DTreeBranchFinderAndReducer<I> branchFinderAndReducer : branchFinderAndReducers) {
            branchFinderAndReducer.getReducer().update(cfg);
        }
        update(cfg);
    }

    private List<DTreeBranchFinderAndReducer<I>> intializeBranchFindersAndReducers(ClassificationCounter classificationCounts,  Map<BranchType, Set<String>> candidateAttributesByBranchType) {
        /**Branch finders should be paired with the correct reducers. With this method, we don't leave open the possibility for a user to make a mistake with the pairings.
         * */
        List<DTreeBranchFinderAndReducer<I>> branchFindersAndReducers = Lists.newArrayList();
        int numClasses = classificationCounts.allClassifications().size();
        Object minorityClassification = ClassificationCounter.getLeastPopularClass(classificationCounts);
        Map<BranchType, DTreeReducer<I>> reducerMap = getReducers(minorityClassification);
        for (BranchFinderBuilder<ClassificationCounter, DTNode> branchFinderBuilder : getBranchFinderBuilders()) {
            if (useBranchFinder(branchFinderBuilder, numClasses)) {
                BranchFinder<ClassificationCounter, DTNode> branchFinder = branchFinderBuilder.buildBranchFinder(classificationCounts, candidateAttributesByBranchType.get(BranchType.BINARY_CATEGORICAL));
                DTreeReducer<I> reducer = reducerMap.get(branchFinderBuilder.getBranchType());
                branchFindersAndReducers.add(new DTreeBranchFinderAndReducer<I>(branchFinder, reducer));
            }
        }
        return branchFindersAndReducers;
    }


    private boolean useBranchFinder(BranchFinderBuilder<ClassificationCounter, DTNode> branchFinderBuilder, int numClasses) {
        if (branchFinderBuilder.getBranchType().equals(BranchType.BINARY_CATEGORICAL) && numClasses != 2) {
            return false;
        }
        if (branchFinderBuilder.getBranchType().equals(BranchType.CATEGORICAL) && numClasses == 2) {
            return false;
        }
        return true;
    }

    protected Map<BranchType, DTreeReducer<I>> getReducers(Object minorityClassification) {
        Map<BranchType, DTreeReducer<I>> reducers = Maps.newHashMap();
        reducers.put(BranchType.BINARY_CATEGORICAL, new BinaryCatBranchReducerReducer<I>(minorityClassification));
        reducers.put(BranchType.CATEGORICAL, new DTCatBranchReducer<I>());
        reducers.put(BranchType.NUMERIC, new DTNumBranchReducer<I>());
        reducers.put(BranchType.BOOLEAN, new DTCatBranchReducer<I>());
        return reducers;
    }

    protected List<DTBranchFinderBuilder> getDefaultBranchFinderBuilders() {
        List<DTBranchFinderBuilder> branchFinderBuilders = Lists.newArrayList();
        branchFinderBuilders.add(new DTBinaryCatBranchFinderBuilder());
        branchFinderBuilders.add(new DTCatBranchFinderBuilder());
        branchFinderBuilders.add(new DTNumBranchFinderBuilder());
        return branchFinderBuilders;
    }


}
