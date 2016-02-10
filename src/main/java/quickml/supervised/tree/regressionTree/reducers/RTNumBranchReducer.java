package quickml.supervised.tree.regressionTree.reducers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.twitter.common.stats.ReservoirSampler;
import com.twitter.common.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexanderhawk on 4/23/15.
 */
public class RTNumBranchReducer<I extends RegressionInstance> extends RTreeReducer<I> {
    private static final Logger logger = LoggerFactory.getLogger(RTNumBranchReducer.class);
    public static final double DOWN_FACTOR = 10E5;
    private Random rand = Random.Util.fromSystemRandom(MapUtils.random);
   //TODO: once verify functionality is correct, remove these variables and get n classification counters which can then be further merged in the branchFinder
    final int numSamplesPerBin;
    final int numNumericBins;

    public RTNumBranchReducer(List<I> trainingData, int numSamplesPerBin, int numNumericBins) {
        super(trainingData);
        this.numSamplesPerBin = numSamplesPerBin;
        this.numNumericBins = numNumericBins;
    }



    @Override
    public Optional<AttributeStats<MeanValueCounter>> getAttributeStats(String attribute) {
        //get List of Classification counters for each bin.  First get bin locations in the data, then loop though the data and get Classification counters by bin
        if (getTrainingData().size() < numNumericBins) {
            return Optional.absent();
        }
        Optional<double[]> splitsOptional = createNumericSplit(getTrainingData(), attribute);
        if (!splitsOptional.isPresent()) {
          //  createNumericSplit(getTrainingData(), attribute);
            return Optional.absent();

        }

        double[] splitPoints = splitsOptional.get();
        return getAttributeStatsOptional(attribute, splitPoints, getTrainingData());
    }

    public static <I extends RegressionInstance> Optional<AttributeStats<MeanValueCounter>> getAttributeStatsOptional(String attribute, double[] splitPoints, List<I> trainingData) {

     //TODO: split points should not be doubles.  They should be Numbers, which can be longs for the case that numeric values are longs.
        List<MeanValueCounter> meanValueCounters = Lists.newArrayListWithCapacity(splitPoints.length + 1);
        MeanValueCounter aggregateStats = new MeanValueCounter();
        double delta = getDelta(splitPoints);
        for (int i = 0; i < splitPoints.length; i++) {
            meanValueCounters.add(new MeanValueCounter(splitPoints[i]));
        }
        meanValueCounters.add(new MeanValueCounter(splitPoints[splitPoints.length - 1] + delta)); //cc holds all vals greater than greatest split point.
        int uncaughtMissingValues = 0;
        for (I instance : trainingData) {
            AttributesMap attributes = instance.getAttributes();
            double attributeVal;
            if (!attributes.containsKey(attribute) || attributes.get(attribute)==null)
                attributeVal=Double.MIN_VALUE; //check old quickml
            else {
                attributeVal = ((Number) (attributes.get(attribute))).doubleValue();
            }

            double threshold = 0, previousThreshold = 0, nextThreshold = 0;
            boolean added = false;

            for (int i = 0; i < splitPoints.length; i++) {
                previousThreshold = threshold;
                threshold = splitPoints[i];
                if (splitPointIsADuplicateOfLast(threshold, previousThreshold, i)) {
                    continue;
                } else if (attributeVal <= threshold + delta){ //total hack, and prevents quickml from working well with fine grained num attributes
                    meanValueCounters.get(i).update(instance.getLabel(), instance.getWeight());
                    added = true;
                    break; //break ensures the instance is added to only one bin.
                }


            }
            if (attributeVal > splitPoints[splitPoints.length - 1] + delta) {
                meanValueCounters.get(splitPoints.length).update(instance.getLabel(), instance.getWeight());
                added = true;
            }
            aggregateStats.update(instance.getLabel(), instance.getWeight());
            if (!added) {
                uncaughtMissingValues++;
            }
        }
        //remove: testCode
        double total = 0;
        for (MeanValueCounter cc : meanValueCounters) {
            total+=cc.getTotal();
        }
        assert total<=aggregateStats.getTotal() +1E-5 && total>= aggregateStats.getTotal() -1E-5;

        if (uncaughtMissingValues > 0) {
            logger.info("uncaught missing values for attribute {} : {}", attribute, uncaughtMissingValues);
        }
        return Optional.of(new AttributeStats<>(meanValueCounters, aggregateStats, attribute));
    }

    private static double getDelta(double[] splitPoints) {
        return (splitPoints.length >= 2) ? (splitPoints[1] -splitPoints[0])/DOWN_FACTOR : splitPoints[0]/DOWN_FACTOR;
    }

    private static boolean splitPointIsADuplicateOfLast(double threshold, double previousThreshold, int i) {
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

    public static <I extends RegressionInstance> Optional<double[]> getDeterministicSplit(List<I> instances, String attribute, int numNumericBins) {
        final ArrayList<Double> splitList = Lists.newArrayList();
        for (final I sample : instances) {
            if (sample.getAttributes().containsKey(attribute)) {
                splitList.add(((Number) (sample.getAttributes().get(attribute))).doubleValue());
            }
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
        if (split.length==1) {
            return false;
        }
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

    public static <I extends RegressionInstance> ReservoirSampler<Double> fillReservoirSampler(List<I> trainingData, String attribute, int desiredSamples) {
        Random rand = Random.Util.fromSystemRandom(MapUtils.random);

        final ReservoirSampler<Double> reservoirSampler = new ReservoirSampler<Double>(desiredSamples + trainingData.size()%desiredSamples, rand);

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
