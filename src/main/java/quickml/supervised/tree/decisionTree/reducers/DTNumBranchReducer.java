package quickml.supervised.tree.decisionTree.reducers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.twitter.common.stats.ReservoirSampler;
import com.twitter.common.util.Random;
import quickml.collections.MapUtils;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static quickml.supervised.tree.constants.ForestOptions.NUM_NUMERIC_BINS;
import static quickml.supervised.tree.constants.ForestOptions.NUM_SAMPLES_PER_NUMERIC_BIN;

/**
 * Created by alexanderhawk on 4/23/15.
 */
public class DTNumBranchReducer<I extends ClassifierInstance> extends DTreeReducer<I> {
    private Random rand = Random.Util.fromSystemRandom(MapUtils.random);
   //TODO: once verify functionality is correct, remove these variables and get n classification counters which can then be further merged in the branchFinder
    int numSamplesPerBin = 17;
    int numNumericBins = 6;

    @Override
    public void updateBuilderConfig(Map<String, Serializable> cfg) {
        if (cfg.containsKey(NUM_SAMPLES_PER_NUMERIC_BIN.name())) {
            numSamplesPerBin = (int) cfg.get(NUM_SAMPLES_PER_NUMERIC_BIN.name());
        }
        if (cfg.containsKey(NUM_NUMERIC_BINS.name())) {
            numNumericBins = (int) cfg.get(NUM_NUMERIC_BINS.name());
        }
    }

    @Override
    public Optional<AttributeStats<ClassificationCounter>> getAttributeStats(String attribute) {
        //get List of Classification counters for each bin.  First get bin locations in the data, then loop though the data and get Classification counters by bin
        if (super.trainingData.size() < numNumericBins) {
            return Optional.absent();
        }
        Optional<double[]> splitsOptional = createNumericSplit(super.trainingData, attribute);
        if (!splitsOptional.isPresent()) {
            return Optional.absent();
        }

        double[] splitPoints = splitsOptional.get();
        return getAttributeStatsOptional(attribute, splitPoints, trainingData);
    }

    public static <I extends ClassifierInstance> Optional<AttributeStats<ClassificationCounter>> getAttributeStatsOptional(String attribute, double[] splitPoints, List<I> trainingData) {
        List<ClassificationCounter> classificationCounters = Lists.newArrayListWithCapacity(splitPoints.length + 1);
        ClassificationCounter aggregateStats = new ClassificationCounter();
        for (int i = 0; i < splitPoints.length; i++) {
            classificationCounters.add(new ClassificationCounter(splitPoints[i]));
        }
        classificationCounters.add(new ClassificationCounter()); //no val is needed for the last cc since no split point can be greater than the values in it.

        for (I instance : trainingData) {

            double attributeVal = ((Number) (instance.getAttributes().get(attribute))).doubleValue();
            double threshold = 0, previousThreshold = 0;

            for (int i = 0; i < splitPoints.length; i++) {
                previousThreshold = threshold;
                threshold = splitPoints[i];
                if (spitPointIsADuplicateOfLast(threshold, previousThreshold, i)) {
                    continue;
                } else if (attributeVal < threshold) {
                    classificationCounters.get(i).addClassification(instance.getLabel(), instance.getWeight());
                    break;
                }
            }
            if (attributeVal > splitPoints[splitPoints.length - 1]) {
                classificationCounters.get(splitPoints.length).addClassification(instance.getLabel(), instance.getWeight());
            }
            aggregateStats.addClassification(instance.getLabel(), instance.getWeight());
        }
        return Optional.of(new AttributeStats<>(classificationCounters, aggregateStats, attribute));
    }

    private static boolean spitPointIsADuplicateOfLast(double threshold, double previousThreshold, int i) {
        return previousThreshold == threshold && i != 0;
    }


    private Optional<double[]> getSplit(ReservoirSampler<Double> reservoirSampler) {
        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final Double sample : reservoirSampler.getSamples()) {
            splitList.add(sample);
        }
        if (splitList.isEmpty() || splitList.size()<numNumericBins) {
            return Optional.absent();
        }
        return getBinDividerPoints(numNumericBins, splitList);
    }

    public static <I extends ClassifierInstance> Optional<double[]> getDeterministicSplit(List<I> instances, String attribute, int numNumericBins) {
        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final I sample : instances) {
            splitList.add(((Number) (sample.getAttributes().get(attribute))).doubleValue());
        }
        if (splitList.isEmpty() || splitList.size()<numNumericBins) {
            return Optional.absent();
        }
        return getBinDividerPoints(numNumericBins, splitList);
    }

    public static Optional<double[]> getBinDividerPoints(int numNumericBins, List<Double> attributeValues) {
        /**Gets the midPoint of the first value in the upper bin and the last value in the lower bin...with values evenly distributed between bins.
            when there is a remainder, the bins with lower index will get 1 additional value.
         */
        Collections.sort(attributeValues);
        int numSplitPoints = numNumericBins-1;
        final double[] split = new double[numSplitPoints];
        final int indexMultiplier = attributeValues.size() / (numNumericBins);  //note indexMultiplier*numericBins < splitListSize => last bin will have more samples (the remainder) than other bins.
        final int remainder = attributeValues.size()%numNumericBins;
        int splitPointIndex = 0;
        int firstIndexOf2ndBin = indexMultiplier;
        for (int upperIndex = firstIndexOf2ndBin; upperIndex < attributeValues.size(); upperIndex+=indexMultiplier) {
            if (splitPointIndex < remainder) {
                upperIndex++;
            }
            split[splitPointIndex] = (attributeValues.get(upperIndex) + attributeValues.get(upperIndex-1))/2.0;
            splitPointIndex++;

        }
        boolean allValuesSame = allValuesSame(split);
        if (allValuesSame) {
            return Optional.absent();
        }
        return Optional.of(split);
    }

    public static boolean allValuesSame(double[] split) {
        boolean allValuesSame = true;
        for (int x = 0; x<split.length-1; x++) {
            if (split[x] != split[x+1])
                allValuesSame = false;
        }
        return allValuesSame;
    }

    private Optional<double[]> createNumericSplit(final List<I> trainingData, final String attribute) {
        int desiredSamples = numSamplesPerBin * numNumericBins;
        if (trainingData.size() <  desiredSamples) {
            return getDeterministicSplit(trainingData, attribute, numNumericBins); //makes code testable, because now can be made deterministic by making numSamplesPerNumericBin < trainingData.getSize.
        }

        final ReservoirSampler<Double> reservoirSampler = fillReservoirSampler(trainingData, attribute, desiredSamples);

        return getSplit(reservoirSampler);
    }

    public static <I extends ClassifierInstance> ReservoirSampler<Double> fillReservoirSampler(List<I> trainingData, String attribute, int desiredSamples) {
        Random rand = Random.Util.fromSystemRandom(MapUtils.random);

        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(desiredSamples, rand);

        int incrementSize = trainingData.size() / desiredSamples;
        for (int i = 0; i < trainingData.size(); i += incrementSize) {
            Serializable value = trainingData.get(i).getAttributes().get(attribute);
            if (value == null) {
                continue;
            }
            reservoirSampler.sample(((Number) value).doubleValue());
        }
        return reservoirSampler;
    }
}
