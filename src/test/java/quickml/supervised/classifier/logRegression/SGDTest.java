package quickml.supervised.classifier.logRegression;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.supervised.classifier.logisticRegression.SGD;
import quickml.supervised.classifier.logisticRegression.SparseClassifierInstance;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by chrisreeves on 10/13/15.
 */
public class SGDTest {
    private  static final Logger logger = LoggerFactory.getLogger(SGDTest.class);

    @Test
    public void testMinimize() throws Exception {
        List<SparseClassifierInstance> instances = new ArrayList<>();
        AttributesMap attributesMap = new AttributesMap();
        attributesMap.put("feature1", 1.0);
        Map<String, Integer> nameToValueMap = new HashMap<>();
        nameToValueMap.put("feature1", 0);

        instances.add(new SparseClassifierInstance(attributesMap, 1.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 1.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));

        SGD sgd = new SGD()
                .maxEpochs(10000)
                .minEpochs(10)
                .costConvergenceThreshold(0.001)
                .weightConvergenceThreshold(0.0001)
                .learningRate(0.1)
                .minibatchSize(4);

        double[] result = sgd.minimize(instances, 1);
        Assert.assertEquals(0.0, result[0], 10E-7);
        //TODO: verify results
        int j= 0;
        for(double value : result) {
           logger.info("value at index {}, {}",j, value);
            j++;
        }
    }

    @Test
    public void testMinimize2Var() throws Exception {
        List<SparseClassifierInstance> instances = new ArrayList<>();
        AttributesMap attributesMap = new AttributesMap();
        attributesMap.put("feature1", 1.0);
        Map<String, Integer> nameToValueMap = new HashMap<>();
        nameToValueMap.put("feature1", 0);

        instances.add(new SparseClassifierInstance(attributesMap, 1.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));

        SGD sgd = new SGD()
                .maxEpochs(2000)
                .minEpochs(100)
                .costConvergenceThreshold(0.001)
                .weightConvergenceThreshold(0.00001)
                .learningRate(0.1)
                .minibatchSize(4)
                .useBoldDriver(false);


        double[] result = sgd.minimize(instances, 1);
        Assert.assertEquals(-1.098612, result[0], 1E-3);
        //TODO: verify results
        int j= 0;
        for(double value : result) {
            logger.info("value at index {}, {}",j, value);
            j++;
        }
    }

    @Test
    public void testIsConverged() throws Exception {
        double weights[] = new double[3];
        double convergenceThreshold = 0.1;
        weights[0] = 1;
        weights[1] = 1;
        weights[2] = 1;

        double newWeights[] = Arrays.copyOf(weights, 3);

        assertTrue(SGD.weightsConverged(weights, newWeights, convergenceThreshold));

        newWeights[0] = weights[0]-convergenceThreshold;
        newWeights[1] = weights[1]-convergenceThreshold;
        newWeights[2] = weights[2]-convergenceThreshold;

        assertTrue(SGD.weightsConverged(weights, newWeights, convergenceThreshold));

        newWeights[0] = weights[0]-convergenceThreshold*2;
        newWeights[1] = weights[1]-convergenceThreshold*2;
        newWeights[2] = weights[2]-convergenceThreshold*2;

        assertFalse(SGD.weightsConverged(weights, newWeights, convergenceThreshold));
    }

    @Test
    public void testGetGradient() throws Exception {

    }

    @Test
    public void testApplyMaxGradientNorm() throws Exception {
        double[] gradient = new double[4];
        gradient[0] = 0.5;
        gradient[1] = 0.75;
        gradient[2] = 0.5;
        gradient[3] = 0.25;

        SGD.applyMaxGradientNorm(0.1, gradient);

        assertEquals(0.15, gradient[0], 0.01);
    }
}