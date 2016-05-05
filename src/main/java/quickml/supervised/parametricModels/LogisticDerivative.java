//package quickml.supervised.classifier.downsampling;
//
//import com.google.common.collect.Lists;
//import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
//import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
//import org.javatuples.Pair;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import quickml.supervised.classifier.logisticRegression.SparseClassifierInstance;
//
//import java.io.Serializable;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//
//import static quickml.MathUtils.cappedlogBase2;
//import static quickml.MathUtils.sigmoid;
//
///**
// * Created by alexanderhawk on 4/1/16.
// */
//public class LogisticDerivative implements OptimizableCostFunction<SparseClassifierInstance> {
//
//    private int executorThreadCount = Runtime.getRuntime().availableProcessors();
//    private ExecutorService executorService=Executors.newFixedThreadPool(executorThreadCount);
//
//
//    public static final String EXPECTED_FRACTION_OF_FEATURES_TO_UPDATE_PER_WORKER = "expectedFractionOfFeaturesToUpdatePerWorker";
//    public static final String EXECUTOR_THREAD_COUNT = "executorThreadCount";
//    public static final String MIN_INSTANCES_FOR_PARELLIZATION = "minInstancesForParrellization";
//    public static final String SPARSE_PARELLIZATION = "sparseParallelization";
//    public static final String OPTIMIZABLE_COST_FUNCTION = "optimizableCostFunction";
//    public static final String MAX_GRADIENT_NORM = "maxGradientNorm";
//
//
//    private double expectedFractionOfFeaturesToUpdatePerWorker = 1.0;
//    private int minInstancesForParrellization = 100;
//    private boolean sparseParallelization = true;
//    private double maxGradientNorm = Double.MAX_VALUE;
//
//
//    private static final Logger logger = LoggerFactory.getLogger(LogisticDerivative.class);
//
//    public LogisticDerivative executorThreadCount(int executorThreadCount) {
//        if (executorThreadCount < this.executorThreadCount) {
//            this.executorThreadCount = executorThreadCount;
//        } else {
//            logger.warn("can't use more executors than cores");
//        }
//        return this;
//    }
//    public LogisticDerivative maxGradientNorm(double maxGradientNorm) {
//        this.maxGradientNorm = maxGradientNorm;
//        return this;
//    }
//    public LogisticDerivative minInstancesForParrellization(int minInstancesForParrellization) {
//        this.minInstancesForParrellization = minInstancesForParrellization;
//        return this;
//    }
//
//    public LogisticDerivative expectedFractionOfFeaturesToUpdatePerWorker(double expectedFractionOfFeaturesToUpdatePerWorker) {
//        this.expectedFractionOfFeaturesToUpdatePerWorker = expectedFractionOfFeaturesToUpdatePerWorker;
//        return this;
//    }
//
//    public LogisticDerivative sparseParallelization(boolean sparseParallelization) {
//        this.sparseParallelization = sparseParallelization;
//        return this;
//    }
//
//    public static double probabilityOfTheNegativeClass(double[] weights, SparseClassifierInstance instance) {
//        return 1.0 - probabilityOfThePositiveClass(weights, instance);
//    }
//
//    public static double probabilityOfThePositiveClass(double[] weights, SparseClassifierInstance instance) {
//        return sigmoid(instance.dotProduct(weights));
//    }
//
//    public void updateBuilderConfig(final Map<String, Serializable> config) {
//
//        if (config.containsKey(SPARSE_PARELLIZATION)) {
//            sparseParallelization((Boolean) config.get(SPARSE_PARELLIZATION));
//        }
//        if (config.containsKey(MIN_INSTANCES_FOR_PARELLIZATION)) {
//            minInstancesForParrellization((Integer) config.get(MIN_INSTANCES_FOR_PARELLIZATION));
//        }
//
//        if (config.containsKey(EXECUTOR_THREAD_COUNT)) {
//            executorThreadCount((Integer) config.get(EXECUTOR_THREAD_COUNT));
//        }
//        if (config.containsKey(EXPECTED_FRACTION_OF_FEATURES_TO_UPDATE_PER_WORKER)) {
//            expectedFractionOfFeaturesToUpdatePerWorker((Double) config.get(EXPECTED_FRACTION_OF_FEATURES_TO_UPDATE_PER_WORKER));
//        }
//         if (config.containsKey(MAX_GRADIENT_NORM)) {
//            maxGradientNorm((Double) config.get(MAX_GRADIENT_NORM));
//            }
//
//    }
//
//        @Override
//    public  double computeCost(List<? extends SparseClassifierInstance> instances, double[] weights, double minPredictedProbablity, double ridge, double lasso) {
//        double cost = 0.0;
//        for (SparseClassifierInstance instance : instances) {
//            if ((double) instance.getLabel() == 1.0) {
//                cost += -cappedlogBase2(probabilityOfThePositiveClass(weights, instance), minPredictedProbablity);
//            } else if ((double) instance.getLabel() == 0.0) {
//                cost += -cappedlogBase2(probabilityOfTheNegativeClass(weights, instance), minPredictedProbablity);
//            }
//        }
//        cost += getRegularizationCost(weights, ridge, lasso);
//        cost /= instances.size();
//
//
//        return cost;
//
//    }
////update SparseLinearSGD to return a list at the correct mini-batch location
//    @Override
//    public void updateGradient(final List<? extends SparseClassifierInstance> sparseClassifierInstances, final double[] fixedWeights) {
//        final int[] threadStartAndStopIndices = getThreadStartIndices(executorThreadCount, minInstancesForParrellization);
//        int actualNumThreads = threadStartAndStopIndices.length - 1;
//        if (sparseParallelization) {
//            sparseCalculationOfGradient(sparseClassifierInstances, fixedWeights, gradient, threadStartAndStopIndices, actualNumThreads);
//        } else {
//            nonSparseCalculationOfGradient(sparseClassifierInstances, fixedWeights, gradient, threadStartAndStopIndices, actualNumThreads);
//
//        }
//        addRegularizationComponentOfTheGradient(weights, gradient, ridge, lasso);
//        normalizeTheGradient(currentMiniBatchSize, maxGradientNorm, gradient);
//    }
//
//
//
//    private static void sparseCalculationOfGradient(final List<? extends SparseClassifierInstance> sparseClassifierInstances, final double[] fixedWeights, double[] gradient, final int[] threadStartAndStopIndices, int actualNumThreads) {
//        List<Future<Int2DoubleOpenHashMap>> contributionsToTheGradient = Lists.newArrayListWithCapacity(actualNumThreads);
//        for (int i = 0; i < actualNumThreads; i++) {
//            final int index = i;
//            contributionsToTheGradient.add(executorService.submit(new Callable<Int2DoubleOpenHashMap>() {
//                @Override
//                public Int2DoubleOpenHashMap call() throws Exception {
//                    expectedFractionOfFeaturesToUpdatePerWorker = 1.0;
//                    try {
//                        Int2DoubleOpenHashMap sparseWorkerContributionToTheGradient = getSparseWorkerContributionToTheGradient(sparseClassifierInstances.subList(threadStartAndStopIndices[index], threadStartAndStopIndices[index + 1]), fixedWeights, expectedFractionOfFeaturesToUpdatePerWorker);
//                        return sparseWorkerContributionToTheGradient;
//
//                    } catch (IllegalArgumentException e) {
//                        logger.info("what?");
//                        throw new RuntimeException(e);
//                    }
//                }
//            }));
//        }
//        sparseReductionToTheGradient(gradient, contributionsToTheGradient);
//    }
//
//    private void nonSparseCalculationOfGradient(final List<? extends SparseClassifierInstance> sparseClassifierInstances, final double[] fixedWeights, double[] gradient, final int[] threadStartAndStopIndices, int actualNumThreads) {
//        List<Future<double[]>> contributionsToTheGradient = Lists.newArrayListWithCapacity(actualNumThreads);
//        for (int i = 0; i < actualNumThreads; i++) {
//            final int index = i;
//            contributionsToTheGradient.add(executorService.submit(new Callable<double[]>() {
//                @Override
//                public double[] call() throws Exception {
//                    expectedFractionOfFeaturesToUpdatePerWorker = 1.0;
//                    return getWorkerContributionToTheGradient(sparseClassifierInstances.subList(threadStartAndStopIndices[index], threadStartAndStopIndices[index + 1]), fixedWeights);
//                }
//            }));
//        }
//        reductionToTheGradient(gradient, contributionsToTheGradient);
//    }
//
//
//
//    static void sparseUpdateUnnormalizedGradientForInstance(double[] weights, Int2DoubleOpenHashMap contributionsToTheGradient,
//                                                            SparseClassifierInstance instance) {
//        //could do this with a map for truly sparse instances...but
//        double postiveClassProbability = probabilityOfThePositiveClass(weights, instance);
//
//
//        Pair<int[], double[]> sparseAttributes = instance.getSparseAttributes();
//        int[] indices = sparseAttributes.getValue0();
//        double[] values = sparseAttributes.getValue1();
//        for (int i = 0; i < indices.length; i++) {
//            int featureIndex = indices[i];
//            contributionsToTheGradient.addTo(featureIndex, gradientContributionOfAFeatureValue((Double) instance.getLabel(), postiveClassProbability, values[i]));
//        }
//    }
//
//    private static double gradientContributionOfAFeatureValue(double label, double postiveClassProbability, double value) {
//        return -(label - postiveClassProbability) * value;
//    }
//
//    static void updateUnnormalizedGradientForInstance(double[] weights, double[] contributionsToTheGradient,
//                                                      SparseClassifierInstance instance) {
//        //could do this with a map for truly sparse instances...but
//        double postiveClassProbability = probabilityOfThePositiveClass(weights, instance);
//        Pair<int[], double[]> sparseAttributes = instance.getSparseAttributes();
//        int[] indices = sparseAttributes.getValue0();
//        double[] values = sparseAttributes.getValue1();
//        for (int i = 0; i < indices.length; i++) {
//            int featureIndex = indices[i];
//            contributionsToTheGradient[featureIndex] += gradientContributionOfAFeatureValue((Double) instance.getLabel(), postiveClassProbability, values[i]);
//        }
//    }
//
//    public  Int2DoubleOpenHashMap getSparseWorkerContributionToTheGradient(List<? extends SparseClassifierInstance> instances, double[] weights, double expectedFractionOfFeaturesToUpdate) {
//        Int2DoubleOpenHashMap contributionsToTheGradient = new Int2DoubleOpenHashMap((int) (expectedFractionOfFeaturesToUpdate * weights.length));
//        contributionsToTheGradient.defaultReturnValue(0.0);
//        for (SparseClassifierInstance instance : instances) {
//            sparseUpdateUnnormalizedGradientForInstance(weights, contributionsToTheGradient, instance);
//        }
//        return contributionsToTheGradient;
//    }
//    public static void sparseReductionToTheGradient(double[] gradient, List<Future<Int2DoubleOpenHashMap>> contributions) {
//        for (Future<Int2DoubleOpenHashMap> contribution : contributions) {
//            addSparseContribution(gradient, contribution);
//        }
//    }
//
//    public static void addSparseContribution(double[] gradient, Future<Int2DoubleOpenHashMap> contributionFuture) {
//        try {
//            Int2DoubleOpenHashMap contribution = contributionFuture.get();
//            for (Int2DoubleMap.Entry entry : contribution.int2DoubleEntrySet()) {
//                gradient[entry.getKey()] += entry.getValue();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static double[] getWorkerContributionToTheGradient(List<? extends SparseClassifierInstance> instances, double[] weights) {
//        double[] contributionsToTheGradient = new double[weights.length];
//        for (SparseClassifierInstance instance : instances) {
//            updateUnnormalizedGradientForInstance(weights, contributionsToTheGradient, instance);
//        }
//        return contributionsToTheGradient;
//    }
//    public static void  reductionToTheGradient(double[] gradient, List<Future<double[]>> contributions) {
//        for (Future<double[]> contribution : contributions) {
//            addContribution(gradient, contribution);
//        }
//    }
//
//
//
//    public static void addRegularizationComponentOfTheGradient(double[] weights, double[] gradient, double ridge, double lasso) {
//        for (int i = 1; i < weights.length; i++) {//start at 1 to skip the bias term
//            double lassoDerivative = lasso;
//            if (weights[i] < 0.0) {
//                lassoDerivative *= -1;
//            }
//            gradient[i] += ridge * weights[i] + lassoDerivative;
//        }
//    }
//
//
//    public static void addContribution(double[] gradient, Future<double[]> contributionFuture) {
//        try {
//            double[] contribution = contributionFuture.get();
//            for (int i = 0; i< gradient.length; i++) {
//                gradient[i] += contribution[i];
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    public static double getRegularizationCost(double[] weights, double ridge, double lasso) {
//        double cost = 0;
//        for (int i = 0; i < weights.length; i++) {
//            cost += weights[i] * weights[i] * ridge / 2.0 + Math.abs(weights[i]) * lasso;
//        }
//        return cost;
//    }
//
//
//    public static void normalizeTheGradient(int minibatchSize, double maxGradientNorm, double[] gradient) {
//        for (int i = 1; i < gradient.length; i++) {
//            gradient[i] /= minibatchSize;
//        }
//        if (maxGradientNorm != Double.MAX_VALUE) {
//            applyMaxGradientNorm(maxGradientNorm, gradient);
//        }
//    }
//
//
//
//
//    public static void applyMaxGradientNorm(double maxGradientNorm, double[] gradient) {
//        double gradientSumOfSquares = 0;
//        for (double g : gradient) {
//            gradientSumOfSquares += Math.pow(g, 2);
//        }
//        double gradientNorm = Math.sqrt(gradientSumOfSquares);
//        if (gradientNorm > maxGradientNorm) {
//            double n = gradientNorm / maxGradientNorm;
//            for (int i = 0; i < gradient.length; i++) {
//                gradient[i] = gradient[i] / Math.sqrt(n);
//            }
//        }
//    }
//
//    //this methods seems wack
//    public  int[] getThreadStartIndices(int numInstances, int actualNumThreads, int minInstancesForParrallization) {
//        if (actualNumThreads < minInstancesForParrallization) {
//            int[] threadStartIndices = new int[2];
//            threadStartIndices[0] = 0;
//            threadStartIndices[1] = actualNumThreads;
//            return threadStartIndices;
//        } else if (actualNumThreads <= executorThreadCount) {
//            actualNumThreads = actualNumThreads;
//            int[] threadStartIndices = new int[actualNumThreads+1];
//            for (int i = 0; i < actualNumThreads; i++) {
//                threadStartIndices[i] = i;
//            }
//            threadStartIndices[actualNumThreads] =actualNumThreads; //could be put in loop but follow the convention of putting final stop index outside
//            return threadStartIndices;
//        }
//
//
//
//        int[] threadStartIndices = new int[executorThreadCount + 1];
//
//        int lowerSamplesPerThread = numInstances / executorThreadCount;
//        int upperSamplesPerThread = numInstances / executorThreadCount + 1;
//        int remainder = numInstances % executorThreadCount;
//        int currentStartIndex = 0;
//        for (int i = 0; i < executorThreadCount; i++) {
//            threadStartIndices[i] = currentStartIndex;
//            if (i >= executorThreadCount - remainder) {
//                currentStartIndex += upperSamplesPerThread;
//            } else {
//                currentStartIndex += lowerSamplesPerThread;
//            }
//        }
//        threadStartIndices[executorThreadCount] = numInstances;
//        return threadStartIndices;
//
//    }
//
//    @Override
//    public void shutdown(){
//        executorService.shutdown();
//    }
//
//}
