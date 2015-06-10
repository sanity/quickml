package quickml.supervised.inspection;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.*;

/**
 * Created by alexanderhawk on 11/14/14.
 */
public class NumericDistributionSampler {

    public Map<Integer, Long> getHistogramOfCountsForValues() {
        return histogramOfCountsForValues;
    }

    Map<Integer, Long> histogramOfCountsForValues = Maps.newHashMap();
    ImmutableRangeMap<Double, Integer> attributeValueRangeMap;
    public static Random rand = new Random();
    double actualSamples = 0;
    int numBins;
    double lowerBound, upperBound;
    private int samplesToDetermineBinWidths;
    int realSizeOfSplitList = 0;
    double[] attributeValuesAtBinBoundaries;

    public NumericDistributionSampler(List<Instance<AttributesMap, Serializable>> instances, int samplesToDraw, String attribute, int numBins) {
        samplesToDetermineBinWidths = instances.size();
        updateDistributionSampler(instances, samplesToDraw, attribute, numBins);
    }

    public NumericDistributionSampler(List<Instance<AttributesMap, Serializable>> instances, double percentageOfAllSamplesToUse, String attribute, int numBins) {
        samplesToDetermineBinWidths = instances.size();
        updateDistributionSampler(instances, percentageOfAllSamplesToUse, attribute, numBins);
    }

    public NumericDistributionSampler(List<Instance<AttributesMap, Serializable>> instances, int samplesToDraw, String attribute, int numBins, int samplesToDetermineBinWidths) {
        this.samplesToDetermineBinWidths = Math.min(samplesToDetermineBinWidths, instances.size());
        updateDistributionSampler(instances, samplesToDraw, attribute, numBins);
    }

    public NumericDistributionSampler(List<Instance<AttributesMap, Serializable>> instances, double percentageOfAllSamplesToUse, String attribute, int numBins, int samplesToDetermineBinWidths) {
        this.samplesToDetermineBinWidths = Math.min(samplesToDetermineBinWidths, instances.size());
        updateDistributionSampler(instances, percentageOfAllSamplesToUse, attribute, numBins);
    }


    public void updateDistributionSampler(List<Instance<AttributesMap, Serializable>> newInstances, double percentageOfAllSamplesToUse, String attribute, int numBins) {
        int samplesToDraw = (int) (percentageOfAllSamplesToUse * newInstances.size());
        updateHistogramOfCountsForValues(newInstances, samplesToDraw, attribute, numBins);
        createAttributeValueRangeMap();
    }

    public void updateDistributionSampler(List<Instance<AttributesMap, Serializable>> newInstances, int samplesToDraw, String attribute, int numBins) {
        updateHistogramOfCountsForValues(newInstances, samplesToDraw, attribute, numBins);
        createAttributeValueRangeMap();
    }

    private void createAttributeValueRangeMap() {
        double currentCount = 0, prevCount = 0;
        ImmutableRangeMap.Builder<Double, Integer> valuesWithProbabilityRangeBuilder = ImmutableRangeMap.builder();
        if (attributeValueRangeMap != null) {
            valuesWithProbabilityRangeBuilder.putAll(attributeValueRangeMap);
        }
        //is this right?
        for (Integer attributeValBinNumber : histogramOfCountsForValues.keySet()) {
            prevCount = currentCount;
            currentCount += histogramOfCountsForValues.get(attributeValBinNumber).doubleValue();
            Range<Double> range = Range.openClosed(prevCount / actualSamples, currentCount / actualSamples); //prevCount/actualSamples is the start of the interval we associate with this attribute value.
            valuesWithProbabilityRangeBuilder.put(range, attributeValBinNumber);
        }
        attributeValueRangeMap = valuesWithProbabilityRangeBuilder.build();
    }

    private void updateSplitList(double[] splitList, List<Instance<AttributesMap, Serializable>> instances, String attribute, int i) {
        Number val = ((Number) (instances.get(i).getAttributes().get(attribute)));
        if (val == null) {
            val = Double.valueOf(0);  //consider making this (here and in the decide function) Double.MAX_VALUE
        }
        splitList[i] = ((Number) val).doubleValue();
        realSizeOfSplitList++;
    }


    private void updateHistogramOfCountsForValues(List<Instance<AttributesMap, Serializable>> instances, int samplesToDraw, String attribute, int numBins) {
        Number val;
        //when the samples to draw are less than half the length of the list
        if (histogramOfCountsForValues.size() == 0) {
            if (samplesToDetermineBinWidths > instances.size()) {
                samplesToDetermineBinWidths = instances.size();
            }

            //put samples in a list of appropriate getSize and sort it.
            double[] splitList = new double[samplesToDetermineBinWidths];
            if (instances.size() < samplesToDetermineBinWidths / 2) {
                int folds = instances.size() / samplesToDraw;
                for (int i = 0; i < instances.size(); i += folds) {
                    updateSplitList(splitList, instances, attribute, i);
                }

            } else {
                for (int i = instances.size() - 1; i >= Math.max(0, instances.size() - samplesToDetermineBinWidths); i--) {
                    updateSplitList(splitList, instances, attribute, i);
                }

            }
            Arrays.sort(splitList, 0, realSizeOfSplitList);

            //get bin boundaries from sorted list
            attributeValuesAtBinBoundaries = new double[numBins + 1];
            attributeValuesAtBinBoundaries[0] = splitList[0];
            attributeValuesAtBinBoundaries[attributeValuesAtBinBoundaries.length - 1] = splitList[splitList.length - 1];
            final int indexMultiplier = realSizeOfSplitList / (numBins);
            for (int x = 1; x < attributeValuesAtBinBoundaries.length - 1; x++) {
                attributeValuesAtBinBoundaries[x] = splitList[x * indexMultiplier - 1];
            }
        }
        //update the counts for values that fall in each bin

        //
        if (instances.size() < samplesToDraw / 2) {
            int folds = instances.size() / samplesToDraw;
            for (int i = 0; i < instances.size(); i += folds) {
                val = ((Number) (instances.get(i).getAttributes().get(attribute)));
                if (val == null)
                    val = Double.valueOf(0);
                updateHistogram(val, histogramOfCountsForValues);
                actualSamples++;
            }
        } else {
            for (int i = instances.size() - 1; i >= Math.max(0, instances.size() - samplesToDraw); i--) {
                val = ((Number) (instances.get(i).getAttributes().get(attribute)));
                if (val == null) {
                    val = Double.valueOf(0);
                }
                updateHistogram(val, histogramOfCountsForValues);
                actualSamples++;
            }
        }
        return;
    }

    private void updateHistogram(Number val, Map<Integer, Long> localHstogramOfCountsForValues) {
        //need to call .get(attributeVal) to get the Range object for that value. But with splits, there is no need for a range object.  We can just climb up till we step over. to find the boundaries.
        //then increment it
        Preconditions.checkState(attributeValuesAtBinBoundaries != null && attributeValuesAtBinBoundaries.length >= 1);
        int binIndex = getBinIndex(val);

        if (localHstogramOfCountsForValues.keySet().contains(binIndex)) {
            localHstogramOfCountsForValues.put(binIndex, localHstogramOfCountsForValues.get(binIndex).longValue() + 1L);
        } else {
            localHstogramOfCountsForValues.put(binIndex, Long.valueOf(1));
        }
    }

    public int getBinIndex(Number val) {

        int binIndex = 0;
        double upper = 0;
        double valDouble = val.doubleValue();
        for (int i = 0; i < attributeValuesAtBinBoundaries.length - 1; i++) {
            binIndex = i;
            upper = attributeValuesAtBinBoundaries[i + 1];//starts at top of bin 1
            if (valDouble <= upper) {
                break;
            }
        }
        return binIndex;
    }

    private double getRandomDoubleInBin(int bin) {
        double lower = attributeValuesAtBinBoundaries[bin];
        double upper = attributeValuesAtBinBoundaries[bin + 1];
        return rand.nextDouble() * (upper - lower) + lower;
    }

    public Number sampleHistogram() {
        int randBin = attributeValueRangeMap.get(rand.nextDouble());
        return getRandomDoubleInBin(randBin);
    }

}