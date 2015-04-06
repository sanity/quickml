package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.BinaryClassAttributeValueIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.scorers.Scorer;
import quickml.supervised.classifier.tree.decisionTree.scorers.ScorerUtils;
import quickml.supervised.classifier.tree.decisionTree.tree.*;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.CategoricalBranch;

import static quickml.supervised.classifier.tree.decisionTree.tree.MissingValue.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class TwoClassCategoricalBranchFinderForGreedyDecisionTree<T extends InstanceWithAttributesMap> extends BranchFinder<T> {

    private BinaryClassAttributeValueIgnoringStrategy<T> attributeValueIgnoringStrategy;
    private Scorer<ClassificationCounter> scorer;
    private int minLeafInstances;
    private Serializable minorityClassification;

    public TwoClassCategoricalBranchFinderForGreedyDecisionTree(BinaryClassAttributeValueIgnoringStrategy attributeValueIgnoringStrategy, AttributeIgnoringStrategy attributeIgnoringStrategy, Scorer<ClassificationCounter> scorer, ImmutableList<String> candidateAttributes, int minLeafInstances) {
        super(candidateAttributes, attributeIgnoringStrategy);
        this.attributeValueIgnoringStrategy = attributeValueIgnoringStrategy;
        this.scorer = scorer;
        this.minLeafInstances = minLeafInstances;
        this.minorityClassification = attributeValueIgnoringStrategy.getMinorityClassification();

    }


    public Optional<Branch> getBranch(Branch parent, List<T> instances, String attribute) {

        final Pair<ClassificationCounter, List<AttributeValueData>> valueOutcomeCountsPairs =
                ClassificationCounter.getSortedListOfAttributeValuesWithClassificationCounters(instances, attribute, minorityClassification);  //returs a list of ClassificationCounterList

        ClassificationCounter falseCounts = new ClassificationCounter(valueOutcomeCountsPairs.getValue0()); //classification counter treating all values the same
        ClassificationCounter trueCounts = new ClassificationCounter(); //the histogram of counts by classification for the in-set

        final List<AttributeValueData> unfilteredAttributeDataList = valueOutcomeCountsPairs.getValue1();
        Serializable lastValOfInset = unfilteredAttributeDataList.get(0).attributeValue;
        double probabilityOfBeingInInset = 0;


        final List<AttributeValueData> attributeValueDataList = filterAttributeValuesWithInsufficientData(unfilteredAttributeDataList);
        if (attributeValueDataList.size() <= 1)
            return null; //there is just 1 value available.

        double bestScore = 0;
        scorer.setIntrinsicValue(ScorerUtils.getIntrinsicValueOfAttributeForClassifier(attributeValueDataList, attributeValueDataList.size()));
        for (final AttributeValueData valueWithClassificationCounter : attributeValueDataList) {
            final ClassificationCounter testValCounts = valueWithClassificationCounter.classificationCounter;

            trueCounts = trueCounts.add(testValCounts);
            falseCounts = falseCounts.subtract(testValCounts);

            if (trueCounts.getTotal() < minLeafInstances || falseCounts.getTotal() < minLeafInstances) {
                continue;
            }

            double thisScore = scorer.scoreSplit(trueCounts, falseCounts, parent.score);
            if (thisScore > bestScore) {
                bestScore = thisScore;
                lastValOfInset = valueWithClassificationCounter.attributeValue;
                probabilityOfBeingInInset = trueCounts.getTotal() / (trueCounts.getTotal() + falseCounts.getTotal());
            }
        }
        final Set<Serializable> trueSet = Sets.newHashSet();

        for (AttributeValueData attributeValueData : attributeValueDataList) {
            trueSet.add(attributeValueData.attributeValue);
            if (attributeValueData.attributeValue.equals(lastValOfInset)) {
                break;
            }
        }

    return (bestScore==0) ? Optional.<Branch> absent() :
            Optional.<Branch> of(new CategoricalBranch(parent, attribute, trueSet, probabilityOfBeingInInset));
}


    private List<AttributeValueData> filterAttributeValuesWithInsufficientData(List<AttributeValueData> valuesWithClassificationCounters) {
        int attributesWithSuffValues = 0;
        List<AttributeValueData> filteredAttributeData = Lists.newArrayList();
        for (final AttributeValueData attributeValueData : valuesWithClassificationCounters) {
            if (attributeValueData.attributeValue.equals(MISSING_VALUE.name())) {
                //ensures missing value always go the way of the outset.
                continue;
            }
            if (this.attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(attributeValueData.classificationCounter)) {
                filteredAttributeData.add(attributeValueData);
            }
        }
        return filteredAttributeData;
    }
}
