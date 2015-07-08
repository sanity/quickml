package quickml.supervised.inspection;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 11/17/14.
 */
public class NumericDistributionSamplerTest {
    private static final Logger logger = LoggerFactory.getLogger(NumericDistributionSamplerTest.class);
    @Test
    public void testStoresCountsCorrectly() {
        List<Instance<AttributesMap, Serializable>> instances = getInstances();
        for (int i = 0; i<2; i++) {
            instances.addAll(getInstances());
        }
        NumericDistributionSampler numericDistributionSampler = new NumericDistributionSampler(instances, 1.0, "v1", 4);
        Map<Integer, Long> distribution =  numericDistributionSampler.getHistogramOfCountsForValues();
        Assert.assertTrue(distribution.get(0).equals(3L));
        Assert.assertTrue(distribution.get(1).equals(3L));
        Assert.assertTrue(distribution.get(2).equals(3L));
        Assert.assertTrue(distribution.get(3).equals(3L));

    }
    @Test
    public void samplesDistCorrectly() {
        List<Instance<AttributesMap, Serializable>> instances = getInstances();
        //add same instances multiple times since we'll be randomly (nearly randomly) removing instances
        for (int i = 0; i<10; i++) {
            instances.addAll(getInstances());
        }
        NumericDistributionSampler numericDistributionSampler = new NumericDistributionSampler(instances, 1.0, "v1", 4);
        Map<Integer, Long> actualHistogram =  numericDistributionSampler.getHistogramOfCountsForValues();
        Map<Integer, Double> sampledDistribution = Maps.newHashMap();//contains bin + counts
        double tolerance = 0.1;
        double samples = 800;

        for (int i = 0; i<samples; i++) {
            double val = (Double) numericDistributionSampler.sampleHistogram();
            int bin = numericDistributionSampler.getBinIndex(val);
            if (sampledDistribution.containsKey(bin)) {
                sampledDistribution.put(bin, (Double) (sampledDistribution.get(bin)).doubleValue() + 1.0);
            } else {
                sampledDistribution.put(bin, 1.0);
            }
        }
        double total = 0;
        for (Integer bin : actualHistogram.keySet()) {
            total += actualHistogram.get(bin).doubleValue();
        }

        Map<Integer, Double> actualDistribution = Maps.newHashMap();
        for (Integer bin : actualHistogram.keySet()) {
            actualDistribution.put(bin, actualHistogram.get(bin).doubleValue()/total);
            sampledDistribution.put(bin, sampledDistribution.get(bin).doubleValue()/samples);
        }

        for(Integer bin : actualDistribution.keySet()) {
            logger.info("for bin: " + bin + ", the actual prob is: " + actualDistribution.get(bin).doubleValue() + ".  Sampled prob is " + sampledDistribution.get(bin).doubleValue());
            Assert.assertTrue(actualDistribution.get(bin).doubleValue() < sampledDistribution.get(bin).doubleValue() + tolerance &&
                    actualDistribution.get(bin).doubleValue() > sampledDistribution.get(bin).doubleValue() - tolerance);
        }
    }


    @Ignore  //this test is needed if the class it tests turns out to be needed

    public void updatesCorrectly() {
        List<Instance<AttributesMap, Serializable>> instances = getInstances();
        //add same instances multiple times since we'll be randomly (nearly randomly) removing instances
        for (int i = 0; i<10; i++) {
            instances.addAll(getInstances());
        }
        NumericDistributionSampler numericDistributionSampler = new NumericDistributionSampler(instances, 1.0, "v1", 4);
        numericDistributionSampler.updateDistributionSampler(getNewInstances(), 1.0, "v1", 4);
        Map<Integer, Long> actualHistogram =  numericDistributionSampler.getHistogramOfCountsForValues();
        Map<Integer, Double> sampledDistribution = Maps.newHashMap();//contains bin + counts
        double tolerance = 0.1;
        double samples = 400;
        for (int i = 0; i<samples; i++) {
            double val = (Double) numericDistributionSampler.sampleHistogram();
            int bin = numericDistributionSampler.getBinIndex(val);
            if (sampledDistribution.containsKey(bin)) {
                sampledDistribution.put(bin, (Double) (sampledDistribution.get(bin)).doubleValue() + 1.0);
            } else {
                sampledDistribution.put(bin, 1.0);
            }
        }
        double total = 0;
        for (Integer bin : actualHistogram.keySet()) {
            total += actualHistogram.get(bin).doubleValue();
        }

        Map<Integer, Double> actualDistribution = Maps.newHashMap();
        for (Integer bin : actualHistogram.keySet()) {
            actualDistribution.put(bin, actualHistogram.get(bin).doubleValue()/total);
            sampledDistribution.put(bin, sampledDistribution.get(bin).doubleValue()/samples);
        }

        for(Integer bin : actualDistribution.keySet()) {
            logger.info("for bin: " + bin + ", the actual prob should be .5 an d is: " + actualDistribution.get(bin).doubleValue() + ".  Sampled prob is " + sampledDistribution.get(bin).doubleValue());
            Assert.assertTrue(actualDistribution.get(bin).doubleValue() < sampledDistribution.get(bin).doubleValue() + tolerance &&
                    actualDistribution.get(bin).doubleValue() > sampledDistribution.get(bin).doubleValue() - tolerance);
        }
    }


    private List<Instance<AttributesMap, Serializable>> getInstances(){
        List<Instance<AttributesMap, Serializable>> instances = Lists.newArrayList();
        //instance 1
        AttributesMap attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", 0.25);
        Instance<AttributesMap, Serializable> instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 2
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", 0.5);
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 3
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", 0.75);
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 4
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", 1.0);
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        return instances;

    }

    private List<Instance<AttributesMap, Serializable>> getNewInstances(){
        List<Instance<AttributesMap, Serializable>> instances = Lists.newArrayList();
        //instance 1
        AttributesMap attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat2");
        Instance<AttributesMap, Serializable> instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 2
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat1");
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 3
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat1");
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,0.75);
        instances.add(instance);

        //instance 4
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat1");
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,0.75);
        instances.add(instance);

        return instances;

    }

}
