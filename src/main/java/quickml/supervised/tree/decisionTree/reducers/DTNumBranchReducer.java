package quickml.supervised.tree.decisionTree.reducers;

import com.google.common.collect.Lists;
import com.twitter.common.stats.ReservoirSampler;
import com.twitter.common.util.Random;
import quickml.collections.MapUtils;
import quickml.data.ClassifierInstance;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;

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
    int numSamplesPerBin = 50;
    int numNumericBins = 6;

    @Override
    public void updateBuilderConfig(Map<String, Object> cfg) {
        if (cfg.containsKey(NUM_SAMPLES_PER_NUMERIC_BIN.name())) {
            numSamplesPerBin = (int) cfg.get(NUM_SAMPLES_PER_NUMERIC_BIN.name());
        }
        if (cfg.containsKey(NUM_NUMERIC_BINS.name())) {
            numNumericBins = (int) cfg.get(NUM_NUMERIC_BINS.name());
        }
    }

    @Override
    public AttributeStats<ClassificationCounter> getAttributeStats(String attribute) {
        //get List of Classification counters for each bin.  First get bin locations in the data, then loop though the data and get Classification counters by bin
        double[] splits = createNumericSplit(super.trainingData, attribute);
        List<ClassificationCounter> classificationCounters = Lists.newArrayListWithCapacity(splits.length + 1);
        ClassificationCounter aggregateStats = new ClassificationCounter();
        for (int i = 0; i < splits.length; i++) {
            classificationCounters.add(new ClassificationCounter(splits[i]));
        }
        classificationCounters.add(new ClassificationCounter()); //no val is needed for the last cc since no split point can be greater than the values in it.

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



    private double[] getSplit(ReservoirSampler<Double> reservoirSampler) {
        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final Double sample : reservoirSampler.getSamples()) {
            splitList.add(sample);
        }
        if (splitList.isEmpty()) {
            throw new RuntimeException("Split list empty");
        }
        Collections.sort(splitList);

        final double[] split = new double[numNumericBins - 1];
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

        final double[] split = new double[numNumericBins - 1];
        final int indexMultiplier = splitList.size() / (split.length + 1);//num elements / num bins
        for (int x = 0; x < split.length && (x + 1) * indexMultiplier < splitList.size(); x++) {
            split[x] = splitList.get((x + 1) * indexMultiplier);
        }
        return split;
    }

    private double[] createNumericSplit(final List<I> trainingData, final String attribute) {
        int numSamples = Math.min(numSamplesPerBin, trainingData.size());
   /*     if (numSamples == trainingData.size()) {
            return getDeterministicSplit(trainingData, attribute); //makes code testable, because now can be made deterministic by making numSamplesPerNumericBin < trainingData.getSize.
        }
*/
        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(numSamples, rand);
        int samplesToSkipPerStep = Math.max(1, trainingData.size() / numSamplesPerBin);
        if (trainingData.size() / numSamplesPerBin == 1) {
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
  /*  private double[] createNumericSplit(final List<T> trainingData, final String attribute) {
        int numSamples = Math.min(RESERVOIR_SIZE, trainingData.size());
        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(numSamples, rand);
        int samplesToSkipPerStep = Math.max(1, trainingData.size() / RESERVOIR_SIZE);
        for (int i=0; i<trainingData.size(); i+=samplesToSkipPerStep) {
            Serializable value = trainingData.get(i).getAttributes().get(attribute);
            if (value == null) {
                continue;
            }
            reservoirSampler.sample(((Number) value).doubleValue());
        }

        return getSplit(reservoirSampler);
    }
    private Map<String, double[]> createNumericSplits(final List<T> trainingData) {
        final Map<String, ReservoirSampler<Double>> rsm = Maps.newHashMap();
        int numSamples = Math.min(RESERVOIR_SIZE, trainingData.size());
        int samplesToSkipPerStep = Math.max(1, trainingData.size() / RESERVOIR_SIZE);

        for (int i=0; i<numSamples; i+=samplesToSkipPerStep) {
            for (final Entry<String, Serializable> attributeEntry : trainingData.get(i).getAttributes().entrySet()) {
                if (attributeEntry.getValue() instanceof Number) {
                    ReservoirSampler<Double> reservoirSampler = rsm.get(attributeEntry.getKey());
                    if (reservoirSampler == null) {
                        reservoirSampler = new ReservoirSampler<>(numSamples, rand);
                        rsm.put(attributeEntry.getKey(), reservoirSampler);
                    }
                    reservoirSampler.sample(((Number) attributeEntry.getValue()).doubleValue());
                }
            }
        }

        final Map<String, double[]> splits = Maps.newHashMap();

        for (final Entry<String, ReservoirSampler<Double>> e : rsm.entrySet()) {
            final double[] split = getSplit(e.getValue());
            splits.put(e.getKey(), split);
        }
        return splits;
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
*/
}
