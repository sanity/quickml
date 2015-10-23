package quickml.supervised.classifier.logRegression;

import com.beust.jcommander.internal.Lists;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.MathUtils;
import quickml.data.AttributesMap;
import quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils;
import quickml.supervised.classifier.logisticRegression.SGD;
import quickml.supervised.classifier.logisticRegression.SparseClassifierInstance;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * Created by chrisreeves on 10/13/15.
 */
public class SGDTest {
    private  static final Logger logger = LoggerFactory.getLogger(SGDTest.class);

    @Test
    public void testMinimizeNoRegularization() throws Exception {
        List<SparseClassifierInstance> instances = new ArrayList<>();
        AttributesMap attributesMap = new AttributesMap();
        attributesMap.put("feature1", 1.0);
        Map<String, Integer> nameToValueMap = new HashMap<>();
        nameToValueMap.put(InstanceTransformerUtils.BIAS_TERM, 0);
        nameToValueMap.put("feature1", 1);

        instances.add(new SparseClassifierInstance(attributesMap, 1.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));

        SGD sgd = new SGD()
                .maxEpochs(2000)
                .minEpochs(800)
                .costConvergenceThreshold(0.001)
                .weightConvergenceThreshold(0.00001)
                .learningRate(0.05)
                .minibatchSize(4)
                .useBoldDriver(false);


        double[] result = sgd.minimize(instances, 2);
        Assert.assertEquals(-1.098612, result[0]+result[1], 1E-3);
        //TODO: verify results
        int j= 0;
        for(double value : result) {
            logger.info("value at index {}, {}",j, value);
            j++;
        }
    }

    @Test
    public void testMinimizeVarWithRidge() throws Exception {
        List<SparseClassifierInstance> instances = new ArrayList<>();
        AttributesMap attributesMap = new AttributesMap();
        attributesMap.put("feature1", 1.0);
        Map<String, Integer> nameToValueMap = new HashMap<>();
        nameToValueMap.put(InstanceTransformerUtils.BIAS_TERM, 0);
        nameToValueMap.put("feature1", 1);

        instances.add(new SparseClassifierInstance(attributesMap, 1.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));
        instances.add(new SparseClassifierInstance(attributesMap, 0.0, nameToValueMap));

        SGD sgd = new SGD()
                .maxEpochs(4000)
                .minEpochs(300)
                .costConvergenceThreshold(0.001)
                .weightConvergenceThreshold(0.00001)
                .learningRate(0.05)
                .minibatchSize(4)
                .useBoldDriver(false)
                .ridgeRegularizationConstant(1)
                .sparseParallelization(true);



        double[] result = sgd.minimize(instances, 2);
        double derivativeOfCostFunction = 4 * MathUtils.sigmoid(result[0] + result[1]) - 1 + result[1];
        Assert.assertEquals(0.0, derivativeOfCostFunction, 1E-3);
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
    public void testGetCurrentMiniBatchSize(){
        int totalNumInstances = 13;
        int miniBatchSize = 5;
        int finalMiniBatchStartIndex = miniBatchSize * 2;
        Assert.assertEquals(3, SGD.getCurrentMiniBatchSize(miniBatchSize, totalNumInstances, finalMiniBatchStartIndex));

    }

    @Test
    public  void testGetThreadStartIndices() {

        int miniBatchStartIndex = 10;
        int minibatchSize = 3;
        int executorThreadCount = 2;
        int minInstancesForParrallization = 0;
        int[] startIndices = SGD.getThreadStartIndices(miniBatchStartIndex, minibatchSize,executorThreadCount, minInstancesForParrallization);
        //3 instances in miniBatch, the last thread should be assigned 2 instances (at index 11, and 12)
        Assert.assertEquals(10, startIndices[0]);
        Assert.assertEquals(11, startIndices[1]);

       //test non parrellization
         minInstancesForParrallization = 100;
         startIndices = SGD.getThreadStartIndices(miniBatchStartIndex, minibatchSize,executorThreadCount, minInstancesForParrallization);

        Assert.assertEquals(10, startIndices[0]);
        Assert.assertEquals(13, startIndices[1]);

        //test executors = minibatch size
        minInstancesForParrallization = 0;
        executorThreadCount = minibatchSize;
        startIndices = SGD.getThreadStartIndices(miniBatchStartIndex, minibatchSize,executorThreadCount, minInstancesForParrallization);

        Assert.assertEquals(10, startIndices[0]);
        Assert.assertEquals(11, startIndices[1]);
        Assert.assertEquals(13, startIndices[3]);

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

    @Test
    public void testGetWorkerContributionToTheGradient(){
        List<SparseClassifierInstance> instances = Lists.newArrayList();

        HashMap<String, Integer> nameToIndexMap = new HashMap<>();
        nameToIndexMap.put(InstanceTransformerUtils.BIAS_TERM, 0);
        nameToIndexMap.put("first", 1);
        nameToIndexMap.put("second", 2);
        AttributesMap attributes = AttributesMap.newHashMap();
        attributes.put("first", 1.0);
        attributes.put("second", 1.0);

        instances.add(new SparseClassifierInstance(attributes, 1.0, nameToIndexMap));
        double[] weights = new double[3];
        weights[0] = 1.0;
        weights[1] = 1.0;
        weights[2] = 1.0;

        //expected gradient is -(1.0- 1/(1+e^-3))*1   =-0.04742587317
        double expectedDerivative = -0.04742587317;
        double[] workerContributionToGradient = SGD.getWorkerContributionToTheGradient(instances, weights);
        Assert.assertEquals(workerContributionToGradient[0], expectedDerivative, 1E-5);
        Assert.assertEquals(workerContributionToGradient[1], expectedDerivative, 1E-5);
        Assert.assertEquals(workerContributionToGradient[2], expectedDerivative, 1E-5);

    }

    @Test
    public void testGetWorkerContributionToTheGradient2(){
        List<SparseClassifierInstance> instances = Lists.newArrayList();

        HashMap<String, Integer> nameToIndexMap = new HashMap<>();
        nameToIndexMap.put(InstanceTransformerUtils.BIAS_TERM, 0);
        nameToIndexMap.put("first", 1);
        nameToIndexMap.put("second", 2);
        AttributesMap attributes = AttributesMap.newHashMap();
        attributes.put("first", 1.0);
        attributes.put("second", 0.5);

        instances.add(new SparseClassifierInstance(attributes, 1.0, nameToIndexMap));
        double[] weights = new double[3];
        weights[0] = 0.0;
        weights[1] = -1.0;
        weights[2] = 0.5;

        //expected gradient is -(1.0- 1/(1+e^0.75))*1   =-0.67917869917
        double expectedDerivativePrefactor = -0.67917869917;
        double[] workerContributionToGradient = SGD.getWorkerContributionToTheGradient(instances, weights);
        Assert.assertEquals(workerContributionToGradient[0], expectedDerivativePrefactor, 1E-5);
        Assert.assertEquals(workerContributionToGradient[1], expectedDerivativePrefactor, 1E-5);
        Assert.assertEquals(workerContributionToGradient[2], 0.5*expectedDerivativePrefactor, 1E-5);

    }

    @Test
    public void testSparseGetWorkerContributionToTheGradient(){
        List<SparseClassifierInstance> instances = Lists.newArrayList();

        HashMap<String, Integer> nameToIndexMap = new HashMap<>();
        nameToIndexMap.put(InstanceTransformerUtils.BIAS_TERM, 0);
        nameToIndexMap.put("first", 1);
        nameToIndexMap.put("second", 2);
        AttributesMap attributes = AttributesMap.newHashMap();
        attributes.put("first", 1.0);
        attributes.put("second", 1.0);

        instances.add(new SparseClassifierInstance(attributes, 1.0, nameToIndexMap));
        double[] weights = new double[3];
        weights[0] = 1.0;
        weights[1] = 1.0;
        weights[2] = 1.0;

        //expected gradient is -(1.0- 1/(1+e^-3))*1   =-0.04742587317
        double expectedDerivative = -0.04742587317;
        Int2DoubleOpenHashMap workerContributionToGradient = SGD.getSparseWorkerContributionToTheGradient(instances, weights, 0);
        Assert.assertEquals(workerContributionToGradient.get(0), expectedDerivative, 1E-5);
        Assert.assertEquals(workerContributionToGradient.get(1), expectedDerivative, 1E-5);
        Assert.assertEquals(workerContributionToGradient.get(2), expectedDerivative, 1E-5);

    }

    @Test
    public void testSparseGetWorkerContributionToTheGradient2(){
        List<SparseClassifierInstance> instances = Lists.newArrayList();

        HashMap<String, Integer> nameToIndexMap = new HashMap<>();
        nameToIndexMap.put(InstanceTransformerUtils.BIAS_TERM, 0);
        nameToIndexMap.put("first", 1);
        nameToIndexMap.put("second", 2);
        AttributesMap attributes = AttributesMap.newHashMap();
        attributes.put("first", 1.0);
        attributes.put("second", 0.5);

        instances.add(new SparseClassifierInstance(attributes, 1.0, nameToIndexMap));
        double[] weights = new double[3];
        weights[0] = 0.0;
        weights[1] = -1.0;
        weights[2] = 0.5;

        //expected gradient is -(1.0- 1/(1+e^0.75))*1   =-0.67917869917
        double expectedDerivativePrefactor = -0.67917869917;
        Int2DoubleOpenHashMap workerContributionToGradient = SGD.getSparseWorkerContributionToTheGradient(instances, weights, 0);
        Assert.assertEquals(workerContributionToGradient.get(0), expectedDerivativePrefactor, 1E-5);
        Assert.assertEquals(workerContributionToGradient.get(1), expectedDerivativePrefactor, 1E-5);
        Assert.assertEquals(workerContributionToGradient.get(2), 0.5*expectedDerivativePrefactor, 1E-5);

    }

    @Test
    public void testSparseReductionToTheGradient() {
        double[] gradient = new double[2];
        List<Future<Int2DoubleOpenHashMap>> contributions = Lists.newArrayList();
        Int2DoubleOpenHashMap int2DoubleOpenHashMap = new Int2DoubleOpenHashMap();
        int2DoubleOpenHashMap.put(1, 1.0);
        contributions.add(new FakeFuture<Int2DoubleOpenHashMap>(int2DoubleOpenHashMap));
        Int2DoubleOpenHashMap int2DoubleOpenHashMap2 = new Int2DoubleOpenHashMap();

        int2DoubleOpenHashMap2.put(1, 0.75);
        int2DoubleOpenHashMap2.put(0, 0.5);
        contributions.add(new FakeFuture<Int2DoubleOpenHashMap>(int2DoubleOpenHashMap2));
       SGD.sparseReductionToTheGradient(gradient, contributions);
        Assert.assertEquals(gradient[0], 0.5, 1E-5);
        Assert.assertEquals(gradient[1], 1.75, 1E-5);

    }
        public static class FakeFuture<T> implements   Future<T> {

            private T int2DoubleOpenHashMap;

            public FakeFuture(T int2DoubleOpenHashMap) {
                this.int2DoubleOpenHashMap = int2DoubleOpenHashMap;
            }

            @Override
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }

                @Override
                public boolean isDone() {
                    return true;
                }

                @Override
                public T get() throws InterruptedException, ExecutionException {
                    return int2DoubleOpenHashMap;
                }

                @Override
                public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return int2DoubleOpenHashMap;
                }
            }

    @Test
    public void testReductionToTheGradient() {
        double[] gradient = new double[2];
        List<Future<double[]>> contributions = Lists.newArrayList();
        double[] grad1 = new double[2];
        grad1[1] = 1.0;
        contributions.add(new FakeFuture<double[]>(grad1));
        double[] grad2 = new double[2];
        grad2[1] = 0.75;
        grad2[0] = 0.5;

        contributions.add(new FakeFuture<double[]>(grad2));
        SGD.reductionToTheGradient(gradient, contributions);
        Assert.assertEquals(gradient[0], 0.5, 1E-5);
        Assert.assertEquals(gradient[1], 1.75, 1E-5);

    }

        }
