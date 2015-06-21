package quickml.supervised.tree.decisionTree.treeBuildContexts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.BranchFinder;
import quickml.supervised.tree.branchFinders.branchFinderBuilders.BranchFinderBuilder;
import quickml.supervised.tree.constants.ForestOptions;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTBinaryCatBranchFinderBuilder;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTCatBranchFinderBuilder;
import quickml.supervised.tree.completeDataSetSummaries.DTreeTrainingDataSurveyor;
import quickml.supervised.tree.decisionTree.branchFinders.branchFinderBuilders.DTNumBranchFinderBuilder;
import quickml.supervised.tree.decisionTree.reducers.*;
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
public class DTreeContextBuilder<I extends ClassifierInstance> extends TreeContextBuilder<Object, I, ClassificationCounter, DTNode> {

    Map<String, Object> cfg = Maps.newHashMap();

    public DTreeContextBuilder<I> maxDepth(int maxDepth) {
        cfg.put(ForestOptions.MAX_DEPTH.name(), maxDepth);
        return this;
    }
    public DTreeContextBuilder<I> ignoreAttributeProbability(int ignoreAttributeProbability) {
        cfg.put(ForestOptions.ATTRIBUTE_IGNORING_STRATEGY.name(), new IgnoreAttributesWithConstantProbability(ignoreAttributeProbability));
        return this;
    }

    public DTreeContextBuilder<I> minSplitFraction(int minSplitFraction) {
        cfg.put(ForestOptions.MIN_SLPIT_FRACTION.name(), minSplitFraction);
        return this;
    }


    public DTreeContextBuilder<I> attributeIgnoringStrategy(AttributeIgnoringStrategy attributeIgnoringStrategy) {
        cfg.put(ForestOptions.ATTRIBUTE_IGNORING_STRATEGY.name(), attributeIgnoringStrategy);
        return this;
    }

    public DTreeContextBuilder<I> attributeValueIgnoringStrategy(AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy) {
        cfg.put(ForestOptions.ATTRIBUTE_VALUE_IGNORING_STRATEGY.name(), attributeValueIgnoringStrategy);
        return this;
    }
//TODO finish adding options

    @Override
    public DTreeContextBuilder<I> createTreeBuildContext() {
        return new DTreeContextBuilder<>();
    }

    @Override
    public DTreeContext<I> buildContext(List<I> trainingData) {
        boolean considerBooleanAttributes = hasBranchFinderBuilder(BranchType.BOOLEAN);
        DTreeTrainingDataSurveyor<I> decTreeTrainingDataSurveyor = new DTreeTrainingDataSurveyor<>(considerBooleanAttributes);
        Map<BranchType, Set<String>> candidateAttributesByBranchType = decTreeTrainingDataSurveyor.groupAttributesByType(trainingData);
        ClassificationCounter classificationCounts= valueCounterProducer.getValueCounter(trainingData);
        List<DTreeBranchFinderAndReducer<I>> branchFinderAndReducers = intializeBranchFindersAndReducers(classificationCounts, candidateAttributesByBranchType);
        updateAll(branchFinderAndReducers);
        return new DTreeContext<I>(classificationCounts.allClassifications(), branchingConditions, scorer, branchFinderAndReducers, leafBuilder, valueCounterProducer);
    }

    private void updateAll(List<DTreeBranchFinderAndReducer<I>> branchFinderAndReducers) {
        for (DTreeBranchFinderAndReducer<I> dTreeBranchFinderAndReducer : branchFinderAndReducers) {
            dTreeBranchFinderAndReducer.getReducer().update(cfg);
        }
        update(cfg);
    }

    private List<DTreeBranchFinderAndReducer<I>> intializeBranchFindersAndReducers(ClassificationCounter classificationCounts,  Map<BranchType, Set<String>> candidateAttributesByBranchType) {
        List<DTreeBranchFinderAndReducer<I>> branchFindersAndReducers = Lists.newArrayList();
        int numClasses = classificationCounts.allClassifications().size();
        Object minorityClassification = ClassificationCounter.getLeastPopularClass(classificationCounts);
        Map<BranchType, DTreeReducer<I>> reducerMap = getReducers(minorityClassification);
        if (getBranchFinderBuilders().isEmpty()) {
            branchFinderBuilders = getDefaultBranchFinderBuilders();
        }
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

    protected <I extends ClassifierInstance> Map<BranchType, DTreeReducer<I>> getReducers(Object minorityClassification) {
        Map<BranchType, DTreeReducer<I>> reducers = Maps.newHashMap();
        reducers.put(BranchType.BINARY_CATEGORICAL, new BinaryCatBranchReducerReducer(minorityClassification));
        reducers.put(BranchType.CATEGORICAL, new DTCatBranchReducer<>());
        reducers.put(BranchType.NUMERIC, new DTNumBranchReducer<>());
        reducers.put(BranchType.BOOLEAN, new DTCatBranchReducer<>());

        return reducers;
    }

    protected <I extends ClassifierInstance> List<BranchFinderBuilder<ClassificationCounter, DTNode>>  getDefaultBranchFinderBuilders() {
        List<BranchFinderBuilder<ClassificationCounter, DTNode>> branchFinderBuilders = Lists.newArrayList();
        branchFinderBuilders.add(new DTBinaryCatBranchFinderBuilder());
        branchFinderBuilders.add(new DTCatBranchFinderBuilder());
        branchFinderBuilders.add(new DTNumBranchFinderBuilder());
        return branchFinderBuilders;
    }


}
