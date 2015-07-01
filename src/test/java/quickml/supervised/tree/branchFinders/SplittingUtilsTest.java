package quickml.supervised.tree.branchFinders;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Sets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.constants.ForestOptions;
import quickml.supervised.tree.decisionTree.attributeValueIgnoringStrategies.BinaryClassAttributeValueIgnoringStrategy;
import quickml.supervised.tree.decisionTree.branchingConditions.DTBranchingConditions;
import quickml.supervised.tree.decisionTree.reducers.BinaryCatBranchReducer;
import quickml.supervised.tree.decisionTree.reducers.DTNumBranchReducer;
import quickml.supervised.tree.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.tree.decisionTree.scorers.InformationGainScorer;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 6/25/15.
 */
public class SplittingUtilsTest {


    @Test
    public void findBestCategoricalSplit() throws Exception {
        List<ClassifierInstance> td = getInstances();
        BinaryCatBranchReducer<ClassifierInstance> reducer = new BinaryCatBranchReducer<>(0.0);
        reducer.setTrainingData(td);
        Optional<AttributeStats<ClassificationCounter>> attributeStatsOptional = reducer.getAttributeStats("t");
        AttributeStats<ClassificationCounter> attStats = attributeStatsOptional.get();
        ClassificationCounter aggregateData = ClassificationCounter.countAll(td);
        BinaryClassAttributeValueIgnoringStrategy attributeValueIgnoringStrategy = new BinaryClassAttributeValueIgnoringStrategy(aggregateData, 0);

        Optional<SplittingUtils.SplitScore> splitScoreOptional = SplittingUtils.splitSortedAttributeStats(attStats, new GiniImpurityScorer(),
                new DTBranchingConditions().minSplitFraction(.25).minLeafInstances(0).minScore(0),
                attributeValueIgnoringStrategy, true);
        catBranchAssertions(splitScoreOptional);

        //change scorer
        splitScoreOptional = SplittingUtils.splitSortedAttributeStats(attStats, new InformationGainScorer(),
                new DTBranchingConditions().minSplitFraction(.25).minLeafInstances(0).minScore(0),
                attributeValueIgnoringStrategy, true);
        catBranchAssertions(splitScoreOptional);
    }

    private void catBranchAssertions(Optional<SplittingUtils.SplitScore> splitScoreOptional) {
        SplittingUtils.SplitScore splitScore;
        Assert.assertTrue(splitScoreOptional.isPresent());
        splitScore = splitScoreOptional.get();
        Assert.assertEquals("last index: " + splitScore.indexOfLastValueCounterInTrueSet, splitScore.indexOfLastValueCounterInTrueSet, 1);
        Assert.assertEquals("probOfTrueSet: " + splitScore.probabilityOfBeingInTrueSet, splitScore.probabilityOfBeingInTrueSet, 0.5, 1E-5);
    }

    @Test
    public void testMinSplitFractionEffect () {
        List<ClassifierInstance> td = getExtendedInstances();
        BinaryCatBranchReducer<ClassifierInstance> reducer = new BinaryCatBranchReducer<>(0.0);
        reducer.setTrainingData(td);
        Optional<AttributeStats<ClassificationCounter>> attStatsOptional = reducer.getAttributeStats("t");
        AttributeStats<ClassificationCounter> attStats = attStatsOptional.get();
        ClassificationCounter aggregateData = ClassificationCounter.countAll(td);
        BinaryClassAttributeValueIgnoringStrategy attributeValueIgnoringStrategy = new BinaryClassAttributeValueIgnoringStrategy(aggregateData, 0);
        reducer.setTrainingData(td);

        Optional<SplittingUtils.SplitScore> splitScoreOptional = SplittingUtils.splitSortedAttributeStats(attStats, new GiniImpurityScorer(),
                new DTBranchingConditions().minSplitFraction(.3).minLeafInstances(0).minScore(0),
                attributeValueIgnoringStrategy, true);
        Assert.assertTrue(splitScoreOptional.isPresent());
        SplittingUtils.SplitScore splitScore = splitScoreOptional.get();
        int subOptimalNumberOfEntriesGivenMinSplitFraction = 2;
        Assert.assertEquals("last index: " + splitScore.indexOfLastValueCounterInTrueSet, splitScore.indexOfLastValueCounterInTrueSet, subOptimalNumberOfEntriesGivenMinSplitFraction);
        Set<Double> expectedTrueSet = Sets.newHashSet();
        Assert.assertTrue(splitScore.trueSet.contains(1.0) && splitScore.trueSet.contains(2.0));
        Assert.assertEquals("probOfTrueSet: " + splitScore.probabilityOfBeingInTrueSet, 3.0 / 8, splitScore.probabilityOfBeingInTrueSet, 1E-5);

    }

    @Test
    public void findBestNumericSplit() throws Exception {
        List<ClassifierInstance> td = getExtendedInstances();

        DTNumBranchReducer<ClassifierInstance> reducer = new DTNumBranchReducer<>();
        Map<String, Object> config  = Maps.newHashMap();
        config.put(ForestOptions.NUM_NUMERIC_BINS.name(), 4);
        config.put(ForestOptions.NUM_SAMPLES_PER_NUMERIC_BIN.name(), 2);
        reducer.updateBuilderConfig(config);

        reducer.setTrainingData(td);
        Optional<AttributeStats<ClassificationCounter>> attStatsOptional = reducer.getAttributeStats("t");
        AttributeStats<ClassificationCounter> attStats = attStatsOptional.get();//should not be absent
        ClassificationCounter aggregateData = ClassificationCounter.countAll(td);
        BinaryClassAttributeValueIgnoringStrategy attributeValueIgnoringStrategy = new BinaryClassAttributeValueIgnoringStrategy(aggregateData, 0);
        Optional<SplittingUtils.SplitScore> splitScoreOptional = SplittingUtils.splitSortedAttributeStats(attStats, new GiniImpurityScorer(),
                new DTBranchingConditions().minSplitFraction(.25).minLeafInstances(0).minScore(0),
                attributeValueIgnoringStrategy, false);
        Assert.assertTrue(splitScoreOptional.isPresent());
        SplittingUtils.SplitScore splitScore = splitScoreOptional.get();
        Assert.assertEquals("last index: " + splitScore.indexOfLastValueCounterInTrueSet, splitScore.indexOfLastValueCounterInTrueSet, 0);
        Assert.assertEquals("probOfTrueSet: " + splitScore.probabilityOfBeingInTrueSet, splitScore.probabilityOfBeingInTrueSet, 0.25, 1E-5);

        //change scorer
        /*
        splitScoreOptional = SplittingUtils.splitSortedAttributeStats(attStats, new InformationGainScorer(),
                new DTBranchingConditions().minSplitFraction(.25).minLeafInstances(0).minScore(0),
                attributeValueIgnoringStrategy);
        Assert.assertTrue(splitScoreOptional.isPresent());
        splitScore = splitScoreOptional.get();
        Assert.assertEquals("last index: " + splitScore.indexOfLastValueCounterInTrueSet, splitScore.indexOfLastValueCounterInTrueSet, 1);
        Assert.assertEquals("probOfTrueSet: " + splitScore.probabilityOfBeingInTrueSet, splitScore.probabilityOfBeingInTrueSet, 0.5, 1E-5);
 */
    }


    public static List<ClassifierInstance> getInstances() {
        List<ClassifierInstance> td = Lists.newArrayList();

        AttributesMap atMap = AttributesMap.newHashMap();
        atMap.put("t", 1.0);
        td.add(new ClassifierInstance(atMap, 0.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", 2.0);
        td.add(new ClassifierInstance(atMap, 0.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", 3.0);
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", 4.0);
        td.add(new ClassifierInstance(atMap, 1.0));
        return td;
    }

    public static List<ClassifierInstance> getExtendedInstances() {
        List<ClassifierInstance> td = getInstances();
        AttributesMap atMap = AttributesMap.newHashMap();

        atMap = AttributesMap.newHashMap();
        atMap.put("t", 5.0);
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", 6.0);
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", 7.0);
        td.add(new ClassifierInstance(atMap, 1.0));

        atMap = AttributesMap.newHashMap();
        atMap.put("t", 8.0);
        td.add(new ClassifierInstance(atMap, 1.0));
        return td;
    }
}