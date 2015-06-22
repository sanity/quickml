package quickml.supervised.inspection;

import com.google.common.collect.*;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.tree.TreeBuilderHelper;
import quickml.supervised.tree.constants.MissingValue;

import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by alexanderhawk on 11/14/14.
 */
public class CategoricalDistributionSampler {
    public Map<Object, Long> getHistogramOfCountsForValues() {
        return histogramOfCountsForValues;
    }

    Map<Object, Long> histogramOfCountsForValues = Maps.newHashMap();
    ImmutableRangeMap<Double, Object> attributeValueRangeMap;
    public static Random rand = new Random();
    double actualSamples = 0;

    public CategoricalDistributionSampler(List<Instance<AttributesMap, Object>> instances, int samplesToDraw, String attribute) {
        updateDistributionSampler(instances, samplesToDraw, attribute);
    }

    public CategoricalDistributionSampler(List<Instance<AttributesMap, Object>> instances, double percentageOfAllSamplesToUse, String attribute) {
        updateDistributionSampler(instances, percentageOfAllSamplesToUse, attribute);
    }


    public void updateDistributionSampler(List<Instance<AttributesMap, Object>> newInstances, double percentageOfAllSamplesToUse, String attribute) {
        int samplesToDraw = (int)(percentageOfAllSamplesToUse * newInstances.size());
        updateHistogramOfCountsForValues(newInstances, samplesToDraw, attribute);
        createAttributeValueRangeMap();
    }

    public void updateDistributionSampler(List<Instance<AttributesMap, Object>> newInstances, int samplesToDraw, String attribute) {
        updateHistogramOfCountsForValues(newInstances, samplesToDraw, attribute);
        createAttributeValueRangeMap();
    }

    private void createAttributeValueRangeMap() {
        double currentCount = 0, prevCount = 0;
        ImmutableRangeMap.Builder<Double, Object> valuesWithProbabilityRangeBuilder = ImmutableRangeMap.builder();
       // if (attributeValueRangeMap!=null) {
       //     valuesWithProbabilityRangeBuilder.putAll(attributeValueRangeMap);
       // }
        for (Object attributeVal : histogramOfCountsForValues.keySet()) {
            prevCount = currentCount;
            currentCount += histogramOfCountsForValues.get(attributeVal).doubleValue();
            Range<Double> range = Range.closedOpen(prevCount/actualSamples, currentCount/actualSamples);
            valuesWithProbabilityRangeBuilder.put(range, attributeVal);
        }
        attributeValueRangeMap = valuesWithProbabilityRangeBuilder.build();
    }

    private void updateHistogramOfCountsForValues(List<Instance<AttributesMap, Object>> instances, int samplesToDraw, String attribute) {
        Object val;
        //when the samples to draw are less than half the length of the list
        if (instances.size() < samplesToDraw / 2) {
            int folds = instances.size() / samplesToDraw;
            for (int i = 0; i < instances.size(); i += folds) {
                val = instances.get(i).getAttributes().get(attribute);
                if (val == null)
                    val = MissingValue.MISSING_VALUE.name();
                updateHistogram(val, histogramOfCountsForValues);
                actualSamples++;
            }
        } else {
            for (int i = instances.size()-1; i >= Math.max(0, instances.size() - samplesToDraw); i--) {
                val = instances.get(i).getAttributes().get(attribute);
                updateHistogram(val, histogramOfCountsForValues);
                actualSamples++;
            }
        }
    }

    private void updateHistogram(Object val, Map<Object, Long> localHstogramOfCountsForValues) {
        if (localHstogramOfCountsForValues.keySet().contains(val)) {
            localHstogramOfCountsForValues.put(val, localHstogramOfCountsForValues.get(val).longValue() + 1L);
        } else {
            localHstogramOfCountsForValues.put(val, Long.valueOf(1));
        }
    }

    public Object sampleHistogram() {
        return attributeValueRangeMap.get(rand.nextDouble());
    }

}
