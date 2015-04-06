package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.collect.Sets;
import org.javatuples.Pair;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.CategoricalBranch;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.tree.decisionTree.tree.Node;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class NClassCategoricalBranchFinderForGreedyDecisionTree {

    private Pair<? extends Branch, Double> createNClassCategoricalNode(Node parent, final String attribute,
                                                                       final Iterable<T> instances) {

        final Set<Serializable> values = getAttributeValues(instances, attribute);


        final Set<Serializable> inValueSet = Sets.newHashSet(); //the in-set

        ClassificationCounter inSetClassificationCounts = new ClassificationCounter(); //the histogram of counts by classification for the in-set

        final Pair<ClassificationCounter, Map<Serializable, ClassificationCounter>> valueOutcomeCountsPair = ClassificationCounter
                .countAllByAttributeValues(instances, attribute);
        ClassificationCounter outSetClassificationCounts = valueOutcomeCountsPair.getValue0(); //classification counter treating all values the same

        final Map<Serializable, ClassificationCounter> valueOutcomeCounts = valueOutcomeCountsPair.getValue1(); //map of value _> classificationCounter
        double insetScore = 0;
        while (true) {
            com.google.common.base.Optional<ScoreValuePair> bestValueAndScore = com.google.common.base.Optional.absent();
            //values should be greater than 1
            for (final Serializable thisValue : values) {
                final ClassificationCounter testValCounts = valueOutcomeCounts.get(thisValue);
                //TODO: the next 3 lines may no longer be needed. Verify.
                if (testValCounts == null || thisValue == null || thisValue.equals(MISSING_VALUE)) {
                    continue;
                }
                if (this.minOccurancesOfAttributeValue > 0) {
                    if (attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(testValCounts)) continue;
                }
                final ClassificationCounter testInCounts = inSetClassificationCounts.add(testValCounts);
                final ClassificationCounter testOutCounts = outSetClassificationCounts.subtract(testValCounts);

                double scoreWithThisValueAddedToInset = scorer.scoreSplit(testInCounts, testOutCounts);

                if (!bestValueAndScore.isPresent() || scoreWithThisValueAddedToInset > bestValueAndScore.get().getScore()) {
                    bestValueAndScore = com.google.common.base.Optional.of(new ScoreValuePair(scoreWithThisValueAddedToInset, thisValue));
                }
            }

            if (bestValueAndScore.isPresent() && bestValueAndScore.get().getScore() > insetScore) {
                insetScore = bestValueAndScore.get().getScore();
                final Serializable bestValue = bestValueAndScore.get().getValue();
                inValueSet.add(bestValue);
                values.remove(bestValue);
                final ClassificationCounter bestValOutcomeCounts = valueOutcomeCounts.get(bestValue);
                inSetClassificationCounts = inSetClassificationCounts.add(bestValOutcomeCounts);
                outSetClassificationCounts = outSetClassificationCounts.subtract(bestValOutcomeCounts);

            } else {
                break;
            }
        }
        if (inSetClassificationCounts.getTotal() < minLeafInstances || outSetClassificationCounts.getTotal() < minLeafInstances) {
            return null;
        }
        //because inSetClassificationCounts is only mutated to better insets during the for loop...it corresponds to the actual inset here.
        double probabilityOfBeingInInset = inSetClassificationCounts.getTotal() / (inSetClassificationCounts.getTotal() + outSetClassificationCounts.getTotal());
        return Pair.with(new CategoricalBranch(parent, attribute, inValueSet, probabilityOfBeingInInset), insetScore);
    }


}
