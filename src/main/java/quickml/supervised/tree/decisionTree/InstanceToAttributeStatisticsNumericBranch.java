package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Lists;
import com.twitter.common.stats.ReservoirSampler;
import com.twitter.common.util.Random;
import quickml.collections.MapUtils;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.branchSplitStatistics.TrainingDataReducer;
import quickml.supervised.tree.nodes.AttributeStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexanderhawk on 4/23/15.
 */
public class InstanceToAttributeStatisticsNumericBranch<I extends ClassifierInstance> extends TrainingDataReducer<Object, I, ClassificationCounter> {
    private Random rand = Random.Util.fromSystemRandom(MapUtils.random);
   //TODO: once verify functionality is correct, remove these variables and get n classification counters which can then be further merged in the branchFinder
    int numSamplesForComputingNumericSplitPoints = 50;
    int ordinalTestSpilts = 6;



    @Override
    public AttributeStats<ClassificationCounter> getAttributeStats(String attribute) {
        //get List of Classification counters for each bin.  First get bin locations in the data, then loop though the data and get Classification counters by bin
        double[] splits = createNumericSplit(super.trainingData, attribute);
        List<ClassificationCounter> classificationCounters = Lists.newArrayListWithCapacity(splits.length + 1);
        ClassificationCounter aggregateStats = new ClassificationCounter();
        for (int i = 0; i < splits.length; i++) {
            classificationCounters.set(i, new ClassificationCounter(splits[i]));
        }
        classificationCounters.set(splits.length, new ClassificationCounter()); //no val is needed for the last cc since no split point can be greater than the values in it.

        for (I instance : trainingData) {

            double attributeVal = ((Number) (instance.getAttributes().get(attribute))).doubleValue();
            double threshold = 0, previousThreshold = 0;

            for (int i = 0; i < splits.length; i++) {
                previousThreshold = threshold;
                threshold = splits[i];
                if (previousThreshold == threshold && i != 0) {
                    continue;
                } else if (attributeVal < threshold) {
                    classificationCounters.get(i).addClassification(attributeVal, instance.getWeight());
                }
            }
            if (threshold > splits[splits.length - 1]) {
                classificationCounters.get(splits.length).addClassification(instance.getLabel(), instance.getWeight());
            }
            aggregateStats.addClassification(instance.getLabel(), instance.getWeight());
        }
        return new AttributeStats<>(classificationCounters, aggregateStats, attribute);
    }


    //all numeric and categorical node
    private double[] createNumericSplit(final List<I> trainingData, final String attribute) {
        int numSamples = Math.min(numSamplesForComputingNumericSplitPoints, trainingData.size());
        if (numSamples == trainingData.size()) {
            return getDeterministicSplit(trainingData, attribute); //makes code testable, because now can be made deterministic by making numSamplesForComputingNumericSplitPoints < trainingData.getSize.
        }

        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(numSamples, rand);
        int samplesToSkipPerStep = Math.max(1, trainingData.size() / numSamplesForComputingNumericSplitPoints);
        if (trainingData.size() / numSamplesForComputingNumericSplitPoints == 1) {
            samplesToSkipPerStep = 2;
        }
        for (int i = 0; i < trainingData.size(); i += samplesToSkipPerStep) {
            Object value = trainingData.get(i).getAttributes().get(attribute);
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

    private double[] getDeterministicSplit(List<I> instances, String attribute) {

        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final I sample : instances) {
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
}
