package quickml.supervised.tree.decisionTree.branchFinders;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import quickml.supervised.tree.scorers.GRImbalancedScorer;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldScorer;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldClassificationCounter;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers.GiniImpurityOldScorer;
import quickml.supervised.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.branchFinders.SortableLabelsCategoricalBranchFinder;
import quickml.supervised.tree.branchFinders.SplittingUtils;
import quickml.supervised.tree.branchingConditions.BranchingConditions;
import quickml.supervised.tree.constants.BranchType;
import quickml.supervised.tree.constants.MissingValue;
import quickml.supervised.tree.decisionTree.nodes.DTCatBranch;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.ScorerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 7/5/15.
 */
public class OldBinCatBranchFinder extends SortableLabelsCategoricalBranchFinder<ClassificationCounter>{
//problem
    private int minDiscreteAttributeValueOccurances = 2;
    private Serializable minorityClassification = 1.0;
    private Serializable majorityClassification = 0.0;
    private double majorityToMinorityRatio = 20.0896;//check
    private int minLeafInstances = 0;
    private boolean penalizeCategoricalSplitsBySplitAttributeIntrinsicValue = true;
    private double degreeOfGainRatioPenalty = 1.0;
    private double minSplitFraction = 0.005;
    private OldScorer oldScorer = new GiniImpurityOldScorer();



    public OldBinCatBranchFinder(Set<String> candidateAttributes, BranchingConditions<ClassificationCounter> branchingConditions,
                                                     ScorerFactory<ClassificationCounter> scorerFactory, AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy,
                                                     AttributeIgnoringStrategy attributeIgnoringStrategy) {
            super(candidateAttributes, branchingConditions, scorerFactory, attributeValueIgnoringStrategy, attributeIgnoringStrategy);
        }



        @Override
        public Optional<? extends quickml.supervised.tree.nodes.Branch<ClassificationCounter>> getBranch(quickml.supervised.tree.nodes.Branch<ClassificationCounter> parent, AttributeStats<ClassificationCounter> attributeStats) {
            if (attributeStats.getStatsOnEachValue().size()<=1) {
                return Optional.absent();
            }

            Optional<SplittingUtils.SplitScore> splitScoreOptional = oldCreateTwoClassCategoricalNode(attributeStats, null, branchingConditions, attributeValueIgnoringStrategy, true);
            if ( splitScoreOptional ==null || !splitScoreOptional.isPresent()) {
                return Optional.absent();
            }
            SplittingUtils.SplitScore splitScore = splitScoreOptional.get();
            return createBranch(parent, attributeStats, splitScore);

        }

    @Override
    public BranchType getBranchType() {
        return BranchType.BINARY_CATEGORICAL;
    }
    @Override
    protected Optional<? extends quickml.supervised.tree.nodes.Branch<ClassificationCounter>> createBranch(quickml.supervised.tree.nodes.Branch<ClassificationCounter> parent, AttributeStats<ClassificationCounter> attributeStats, SplittingUtils.SplitScore splitScore) {
        return Optional.of(new DTCatBranch(parent, attributeStats.getAttribute(), splitScore.trueSet,
                splitScore.probabilityOfBeingInTrueSet, splitScore.score,
                attributeStats.getAggregateStats()));
    }

    public  Optional<SplittingUtils.SplitScore> oldCreateTwoClassCategoricalNode(AttributeStats<ClassificationCounter> attributeStats, GRImbalancedScorer<ClassificationCounter> scorer,
                                                                                  BranchingConditions<ClassificationCounter> branchingConditions,
                                                                                  AttributeValueIgnoringStrategy<ClassificationCounter> attributeValueIgnoringStrategy,
                                                                                  boolean doNotUseAttributeValuesWithInsuffientStatistics) {  //Node parent, final String attribute, final Iterable<T> instances) {

       //use the sorted list of cc's i get.  or add a typeless method to attributeStatsProducer to hold the instances? To complicated for now.
        List<ClassificationCounter> ccs = attributeStats.getStatsOnEachValue();
        ClassificationCounter outCounts = attributeStats.getAggregateStats();
        ClassificationCounter inCounts = outCounts.subtract(outCounts); //empty true Set
        Set<String> exemptAttributes = Sets.newHashSet();

        double bestScore = 0;


        double numTrainingExamples = outCounts.getTotal();

        Serializable lastValOfInset = ccs.get(0).attrVal;
        double probabilityOfBeingInInset = 0;
        int valuesInTheInset = 0;

        int attributesWithSufficientValues = labelAttributeValuesWithInsufficientData(ccs);
        if (attributesWithSufficientValues <= 1)
            return null; //there is just 1 value available.
        double intrinsicValueOfAttribute = getIntrinsicValueOfAttribute(ccs, numTrainingExamples);

        for (final ClassificationCounter cc : ccs) {
            if (cc == null || cc.attrVal.equals(MissingValue.MISSING_VALUE)) { // Also a kludge, figure out why
                continue;
            }
            if (this.minDiscreteAttributeValueOccurances > 0) {
                if (!cc.hasSufficientData()) continue;
            }
            inCounts = inCounts.add(cc);
            outCounts = outCounts.subtract(cc);

            double numInstances = inCounts.getTotal() + outCounts.getTotal();
            if (!exemptAttributes.contains(attributeStats.getAttribute()) && (inCounts.getTotal()/ numInstances <minSplitFraction ||
                    outCounts.getTotal()/ numInstances < minSplitFraction)) {
                continue;
            }

            if (inCounts.getTotal() < minLeafInstances || outCounts.getTotal() < minLeafInstances) {
                continue;
            }

            double thisScore = this.oldScorer.scoreSplit(new OldClassificationCounter(inCounts),
                    new OldClassificationCounter(outCounts));
            valuesInTheInset++;
            if (penalizeCategoricalSplitsBySplitAttributeIntrinsicValue) {
                thisScore = thisScore * (1 - degreeOfGainRatioPenalty) + degreeOfGainRatioPenalty * (thisScore / intrinsicValueOfAttribute);            }

            if (thisScore > bestScore) {
                bestScore = thisScore;
                lastValOfInset = cc.attrVal;
                probabilityOfBeingInInset = inCounts.getTotal() / (inCounts.getTotal() + outCounts.getTotal());
            }
        }
        final Set<Serializable> inSet = Sets.newHashSet();
        final Set<Serializable> outSet = Sets.newHashSet();
        boolean insetIsBuiltNowBuildingOutset = false;
        inCounts = new ClassificationCounter();
        outCounts = new ClassificationCounter();

        int indexOfLastValueCounterInTrueSet = 0;
        for (ClassificationCounter cc : ccs) {
            if (!insetIsBuiltNowBuildingOutset) {
                indexOfLastValueCounterInTrueSet++;
            }
            if (!insetIsBuiltNowBuildingOutset && cc.hasSufficientData()) {
                inSet.add(cc.attrVal);
                inCounts.add(cc);
                if (cc.getAttrVal().equals(lastValOfInset)) {
                    insetIsBuiltNowBuildingOutset = true;
                }
            } else {
                outCounts.add(cc);
            }
        }
        if (bestScore==0)
            return Optional.absent();
        else {
            return Optional.of(new SplittingUtils.SplitScore(bestScore, indexOfLastValueCounterInTrueSet, probabilityOfBeingInInset, inSet));
        }
    }

    private int labelAttributeValuesWithInsufficientData(List<ClassificationCounter>  valuesWithClassificationCounters) {
        int attributesWithSuffValues = 0;
        for (final ClassificationCounter cc : valuesWithClassificationCounters) {
            if (this.minDiscreteAttributeValueOccurances > 0) {
                if (attributeValueOrIntervalOfValuesHasInsufficientStatistics(cc)) {
                    cc.setHasSufficientData(false);
                } else {
                    attributesWithSuffValues++;
                }
            } else {
                attributesWithSuffValues++;
            }
        }

        return attributesWithSuffValues;
    }

    private double getIntrinsicValueOfAttribute(List<ClassificationCounter> valuesWithCCs, double numTrainingExamples) {
        double informationValue = 0;
        double attributeValProb = 0;

        for (ClassificationCounter classificationCounter : valuesWithCCs) {
            attributeValProb = classificationCounter.getTotal() / (numTrainingExamples);//-insufficientDataInstances);
            informationValue -= attributeValProb * Math.log(attributeValProb) / Math.log(2);
        }

        return informationValue;
    }

    private boolean attributeValueOrIntervalOfValuesHasInsufficientStatistics(final ClassificationCounter testValCounts) {
        Preconditions.checkArgument(majorityClassification != null && minorityClassification != null);
        Map<Serializable, Double> counts = testValCounts.getCounts();
        if (counts.containsKey(minorityClassification) &&
                counts.get(minorityClassification) > minDiscreteAttributeValueOccurances) {
            return false;
        }

        if (counts.containsKey(majorityClassification) &&
                counts.get(majorityClassification) > majorityToMinorityRatio * minDiscreteAttributeValueOccurances) {
            return false;
        }

        if (hasBothMinorityAndMajorityClassifications(counts)
                && hasSufficientStatisticsForBothClassifications(counts)) {
            return false;
        }

        return true;
    }
    private boolean hasSufficientStatisticsForBothClassifications(Map<Serializable, Double> counts) {
        return counts.get(majorityClassification) > 0.6 * majorityToMinorityRatio * minDiscreteAttributeValueOccurances
                && counts.get(minorityClassification) > 0.6 * minDiscreteAttributeValueOccurances;
    }

    private boolean hasBothMinorityAndMajorityClassifications(Map<Serializable, Double> counts) {
        return counts.containsKey(majorityClassification) && counts.containsKey(minorityClassification);
    }


}
