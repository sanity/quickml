package quickml.supervised.classifier.tree.decisionTree.tree.nodes.branchFinders;

import com.google.common.collect.Lists;
import com.twitter.common.stats.ReservoirSampler;
import com.twitter.common.util.Random;
import org.javatuples.Pair;
import quickml.collections.MapUtils;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCountingPair;
import quickml.supervised.classifier.tree.decisionTree.tree.Node;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.NumericBranch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alexanderhawk on 4/5/15.
 */
public class NumericBranchFinderForGreedyDecisionTree {

    private Random rand = Random.Util.fromSystemRandom(MapUtils.random);


    //all numeric and categorical node
    private double[] createNumericSplit(final List<T> trainingData, final String attribute) {
        int numSamples = Math.min(numSamplesForComputingNumericSplitPoints, trainingData.size());
        if (numSamples == trainingData.size()) {
            return getDeterministicSplit(trainingData, attribute); //makes code testable, because now can be made deterministic by making numSamplesForComputingNumericSplitPoints < trainingData.size.
        }

        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(numSamples, rand);
        int samplesToSkipPerStep = Math.max(1, trainingData.size() / numSamplesForComputingNumericSplitPoints);
        if (trainingData.size() / numSamplesForComputingNumericSplitPoints == 1) {
            samplesToSkipPerStep = 2;
        }
        for (int i = 0; i < trainingData.size(); i += samplesToSkipPerStep) {
            Serializable value = trainingData.get(i).getAttributes().get(attribute);
            if (value == null) {
                continue;
            }
            reservoirSampler.sample(((Number) value).doubleValue());
        }

        return getSplit(reservoirSampler);
    }


    private double[] getSplit(ReservoirSampler<Double> reservoirSampler) {
        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final Double sample : reservoirSampler.getSamples()) {
            splitList.add(sample);
        }
        if (splitList.isEmpty()) {
            throw new RuntimeException("Split list empty");
        }
        Collections.sort(splitList);

        final double[] split = new double[ordinalTestSpilts - 1];
        final int indexMultiplier = splitList.size() / (split.length + 1);//num elements / num bins
        for (int x = 0; x < split.length; x++) {
            split[x] = splitList.get((x + 1) * indexMultiplier);
        }
        return split;
    }

    private double[] getDeterministicSplit(List<T> instances, String attribute) {

        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final T sample : instances) {
            splitList.add(((Number) (sample.getAttributes().get(attribute))).doubleValue());
        }
        if (splitList.isEmpty()) {
            throw new RuntimeException("Split list empty");
        }
        Collections.sort(splitList);

        final double[] split = new double[ordinalTestSpilts - 1];
        final int indexMultiplier = splitList.size() / (split.length + 1);//num elements / num bins
        for (int x = 0; x < split.length && (x + 1) * indexMultiplier < splitList.size(); x++) {
            split[x] = splitList.get((x + 1) * indexMultiplier);
        }
        return split;
    }

    private Pair<? extends Branch, Double> createNumericBranch(Node parent, final String attribute,
                                                               List<T> instances) {

        double bestScore = 0;
        double bestThreshold = 0;
        double probabilityOfBeingInInset = 0;

        final double[] splits = createNumericSplit(instances, attribute);
        List<ClassificationCountingPair> classificationCounts = Lists.newArrayList();
        for (int i = 0; i < splits.length; i++) {
            classificationCounts.add(new ClassificationCountingPair(new HashMap<Serializable, Long>(), new HashMap<Serializable, Long>()));
        }
        for (T instance : instances) {

            double attributeVal = ((Number) (instance.getAttributes().get(attribute))).doubleValue();
            double threshold = 0, previousThreshold = 0;

            for (int i = 0; i < splits.length; i++) {
                previousThreshold = threshold;
                threshold = splits[i];
                if (previousThreshold == threshold && i != 0)
                    continue;

                ClassificationCountingPair classificationCountingPair = classificationCounts.get(i);
                if (attributeVal > threshold) {
                    updateCounts(instance.getLabel(), classificationCountingPair.inCounter);
                } else {
                    updateCounts(instance.getLabel(), classificationCountingPair.outCounter);
                }
            }
        }

        for (int i = 0; i < splits.length; i++) {

            double threshold = splits[i];
            ClassificationCounter inClassificationCounts = new ClassificationCounter(classificationCounts.get(i).inCounter);
            ClassificationCounter outClassificationCounts = new ClassificationCounter(classificationCounts.get(i).outCounter);

            if (classificationProperties.classificationsAreBinary()) {

                if (attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(inClassificationCounts)
                        || inClassificationCounts.getTotal() < minLeafInstances
                        || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(outClassificationCounts)
                        || outClassificationCounts.getTotal() < minLeafInstances) {
                    continue;
                }
            } else if (attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(inClassificationCounts) || attributeValueIgnoringStrategy.shouldWeIgnoreThisValue(outClassificationCounts)) {
                continue;
            }


            double thisScore = scorer.scoreSplit(inClassificationCounts, outClassificationCounts);
            if (thisScore > bestScore) {
                bestScore = thisScore;
                bestThreshold = threshold;
                probabilityOfBeingInInset = inClassificationCounts.getTotal() / (inClassificationCounts.getTotal() + outClassificationCounts.getTotal());
            }
        }

        if (bestScore == 0) {
            return null;
        }

        return Pair.with(new NumericBranch(parent, attribute, bestThreshold, probabilityOfBeingInInset), bestScore);
    }


    private void updateCounts(Serializable label, HashMap<Serializable, Long> mapOfCounts) {
        long previousCounts = mapOfCounts.containsKey(label) ? mapOfCounts.get(label) : 0L;
        mapOfCounts.put(label, previousCounts + 1L);
    }


}
