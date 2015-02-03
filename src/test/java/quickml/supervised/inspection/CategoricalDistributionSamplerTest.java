package quickml.supervised.inspection;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import org.junit.Assert;
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
public class CategoricalDistributionSamplerTest {
    private static final Logger logger = LoggerFactory.getLogger(CategoricalDistributionSamplerTest.class);
    @Test
    public void testStoresCountsCorrectly() {
        List<Instance<AttributesMap, Serializable>> instances = getInstances();
        CategoricalDistributionSampler categoricalDistributionSampler = new CategoricalDistributionSampler(instances, 1.0, "v1");
        Map<Serializable, Long> distribution =  categoricalDistributionSampler.getHistogramOfCountsForValues();
        Assert.assertTrue(distribution.get("cat1").equals(1L));
        Assert.assertTrue(distribution.get("cat2").equals(3L));

    }
    @Test
    public void samplesDistCorrectly() {
        List<Instance<AttributesMap, Serializable>> instances = getInstances();
        //add same instances multiple times since we'll be randomly (nearly randomly) removing instances
        for (int i = 0; i<10; i++) {
            instances.addAll(getInstances());
        }
        CategoricalDistributionSampler categoricalDistributionSampler = new CategoricalDistributionSampler(instances, .5, "v1");
        Map<Serializable, Long> actualHistogram =  categoricalDistributionSampler.getHistogramOfCountsForValues();
        Map<Serializable, Double> sampledDistribution = Maps.newHashMap();
        double tolerance = 0.1;
        double samples = 800;
        for (int i = 0; i<samples; i++) {
            String val = (String) categoricalDistributionSampler.sampleHistogram();
            if (sampledDistribution.containsKey(val)) {
                sampledDistribution.put(val, (Double) (sampledDistribution.get(val)).doubleValue() + 1.0);
            } else {
                sampledDistribution.put(val, 1.0);
            }
        }
        double total = 0;
        for (Serializable val : actualHistogram.keySet()) {
            total += actualHistogram.get(val).doubleValue();
        }
        Map<Serializable, Double> actualDistribution = Maps.newHashMap();
        for (Serializable val : actualHistogram.keySet()) {
            actualDistribution.put(val, actualHistogram.get(val).doubleValue()/total);
            sampledDistribution.put(val, sampledDistribution.get(val).doubleValue()/samples);
        }

        for(Serializable val : actualDistribution.keySet()) {
            logger.info("for val: " + val + ", the actual prob is: " + actualDistribution.get(val).doubleValue() + ".  Sampled prob is " + sampledDistribution.get(val).doubleValue());
            Assert.assertTrue(actualDistribution.get(val).doubleValue() < sampledDistribution.get(val).doubleValue() + tolerance &&
                    actualDistribution.get(val).doubleValue() > sampledDistribution.get(val).doubleValue() - tolerance);
        }
    }

    @Test
    public void updatesCorrectly() {
        List<Instance<AttributesMap, Serializable>> instances = getInstances();
        CategoricalDistributionSampler categoricalDistributionSampler = new CategoricalDistributionSampler(instances, 1.0, "v1");
        categoricalDistributionSampler.updateDistributionSampler(getNewInstances(), 1.0, "v1");
        Map<Serializable, Long> actualHistogram =  categoricalDistributionSampler.getHistogramOfCountsForValues();
        Map<Serializable, Double> sampledDistribution = Maps.newHashMap();
        double tolerance = 0.1;
        double samples = 400;
        for (int i = 0; i<samples; i++) {
            String val = (String) categoricalDistributionSampler.sampleHistogram();
            if (sampledDistribution.containsKey(val)) {
                sampledDistribution.put(val, (Double) (sampledDistribution.get(val)).doubleValue() + 1.0);
            } else {
                sampledDistribution.put(val, 1.0);
            }
        }
        double total = 0;
        for (Serializable val : actualHistogram.keySet()) {
            total += actualHistogram.get(val).doubleValue();
        }
        Map<Serializable, Double> actualDistribution = Maps.newHashMap();
        for (Serializable val : actualHistogram.keySet()) {
            actualDistribution.put(val, actualHistogram.get(val).doubleValue()/total);
            sampledDistribution.put(val, sampledDistribution.get(val).doubleValue()/samples);
        }

        for(Serializable val : actualDistribution.keySet()) {
            logger.info("for val: " + val + ", the actual prob should be .5 an d is: " + actualDistribution.get(val).doubleValue() + ".  Sampled prob is " + sampledDistribution.get(val).doubleValue());
            Assert.assertTrue(actualDistribution.get(val).doubleValue() < sampledDistribution.get(val).doubleValue() + tolerance &&
                    actualDistribution.get(val).doubleValue() > sampledDistribution.get(val).doubleValue() - tolerance);
        }
    }


    private List<Instance<AttributesMap, Serializable>> getInstances(){
        List<Instance<AttributesMap, Serializable>> instances = Lists.newArrayList();
        //instance 1
        AttributesMap attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat1");
        Instance<AttributesMap, Serializable> instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 2
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat2");
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 3
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat2");
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 4
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat2");
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
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        //instance 4
        attributesMap = AttributesMap.newHashMap();
        attributesMap.put("v1", "cat1");
        instance = new InstanceImpl<AttributesMap, Serializable>(attributesMap,1.0);
        instances.add(instance);

        return instances;

    }

}
