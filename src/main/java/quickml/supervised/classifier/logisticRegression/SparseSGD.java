package quickml.supervised.classifier.logisticRegression;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2DoubleMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static quickml.MathUtils.cappedlogBase2;
import static quickml.MathUtils.sigmoid;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class SparseSGD implements GradientDescent<SparseClassifierInstance > {

    private int executorThreadCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;
    public static final String RIDGE = "ridge";
    public static final String LASSO = "lasso";

    public static final Logger logger = LoggerFactory.getLogger(SparseSGD.class);
    public static final String LEARNING_RATE = "learningRate";
    public static final String USE_BOLD_DRIVER = "useBoldDriver";

    public static final String MAX_EPOCHS = "maxEpochs";
    public static final String MIN_EPOCHS = "minEpochs";
    public static final String MINI_BATCH_SIZE = "miniBatchSize";
    public static final String COST_CONVERGENCE_THRESHOLD = "costConvergenceThreshold";
    public static final String LEARNING_RATE_BOOST_FACTOR = "learningRateBoostFactor";
    public static final String LEARNING_RATE_REDUCTION_FACTOR = "learningRateReductionFactor";
    public static final String MAX_GRADIENT_NORM = "maxGradientNorm";
    public static final String WEIGHT_CONVERGENCE_THRESHOLD = "weightConvergenceThreshold";
    public static final String MIN_PREDICTED_PROBABILITY = "minPredictedProbablity";
    public static final String EXPECTED_FRACTION_OF_FEATURES_TO_UPDATE_PER_WORKER = "expectedFractionOfFeaturesToUpdatePerWorker";
    public static final String EXECUTOR_THREAD_COUNT = "executorThreadCount";
    public static final String MIN_INSTANCES_FOR_PARELLIZATION = "minInstancesForParrellization";
    public static final String SPARSE_PARELLIZATION = "sparseParallelization";

    //model hyper-params
    double ridge = 0;
    double lasso = 0;

    //training hyper-params
    private int minibatchSize = 1;
    private int maxEpochs = 8;
    private int minEpochs = 3;

    private double weightConvergenceThreshold = 0.001;
    private double costConvergenceThreshold = 0.001;

    private double learningRate = 10E-5;
    private double maxGradientNorm = Double.MAX_VALUE;
    private double minPredictedProbablity = 10E-6;
    private double learningRateReductionFactor = 0.5;
    private double learningRateBoostFactor = 1.07;
    private boolean useBoldDriver = false;
    private double expectedFractionOfFeaturesToUpdatePerWorker = 1.0;
    private int minInstancesForParrellization = 100;
    private boolean sparseParallelization = true;


    public SparseSGD() {
    }

    public void updateBuilderConfig(final Map<String, Serializable> config) {

        if (config.containsKey(LASSO)) {
            ridgeRegularizationConstant((Double) config.get(LASSO));
        }
        if (config.containsKey(RIDGE)) {
            lassoRegularizationConstant((Double) config.get(RIDGE));
        }
        if (config.containsKey(EXECUTOR_THREAD_COUNT)) {
            executorThreadCount((Integer) config.get(EXECUTOR_THREAD_COUNT));
        }
        if (config.containsKey(EXPECTED_FRACTION_OF_FEATURES_TO_UPDATE_PER_WORKER)) {
            expectedFractionOfFeaturesToUpdatePerWorker((Double) config.get(EXPECTED_FRACTION_OF_FEATURES_TO_UPDATE_PER_WORKER));
        }
        if (config.containsKey(LEARNING_RATE)) {
            learningRate((Double) config.get(LEARNING_RATE));
        }

        if (config.containsKey(USE_BOLD_DRIVER)) {
            useBoldDriver((Boolean) config.get(USE_BOLD_DRIVER));
        }
        if (config.containsKey(MAX_EPOCHS)) {
            maxEpochs((Integer) config.get(MAX_EPOCHS));
        }
        if (config.containsKey(MIN_EPOCHS)) {
            minEpochs((Integer) config.get(MIN_EPOCHS));
        }
        if (config.containsKey(MINI_BATCH_SIZE)) {
            minibatchSize((Integer) config.get(MINI_BATCH_SIZE));
        }
        if (config.containsKey(COST_CONVERGENCE_THRESHOLD)) {
            costConvergenceThreshold((Double) config.get(COST_CONVERGENCE_THRESHOLD));
        }
        if (config.containsKey(LEARNING_RATE_BOOST_FACTOR)) {
            learningRateBoostFactor((Double) config.get(LEARNING_RATE_BOOST_FACTOR));
        }
        if (config.containsKey(LEARNING_RATE_REDUCTION_FACTOR)) {
            learningRateReductionFactor((Double) config.get(LEARNING_RATE_REDUCTION_FACTOR));
        }
        if (config.containsKey(MAX_GRADIENT_NORM)) {
            maxGradientNorm((Double) config.get(MAX_GRADIENT_NORM));
        }
        if (config.containsKey(WEIGHT_CONVERGENCE_THRESHOLD)) {
            weightConvergenceThreshold((Double) config.get(WEIGHT_CONVERGENCE_THRESHOLD));
        }
        if (config.containsKey(MIN_PREDICTED_PROBABILITY)) {
            minPredictedProbablity((Double) config.get(MIN_PREDICTED_PROBABILITY));
        }
        if (config.containsKey(MIN_INSTANCES_FOR_PARELLIZATION)) {
            minInstancesForParrellization((Integer) config.get(MIN_INSTANCES_FOR_PARELLIZATION));
        }
        if (config.containsKey(SPARSE_PARELLIZATION)) {
            sparseParallelization((Boolean) config.get(SPARSE_PARELLIZATION));
        }

    }

    public SparseSGD sparseParallelization(boolean sparseParallelization) {
        this.sparseParallelization = sparseParallelization;
        return this;
    }

    public SparseSGD executorThreadCount(int executorThreadCount) {
        if (executorThreadCount < this.executorThreadCount) {
            this.executorThreadCount = executorThreadCount;
        } else {
            logger.warn("can't use more executors than cores");
        }
        return this;
    }

    public SparseSGD minInstancesForParrellization(int minInstancesForParrellization) {
        this.minInstancesForParrellization = minInstancesForParrellization;
        return this;
    }

    public SparseSGD expectedFractionOfFeaturesToUpdatePerWorker(double expectedFractionOfFeaturesToUpdatePerWorker) {
        this.expectedFractionOfFeaturesToUpdatePerWorker = expectedFractionOfFeaturesToUpdatePerWorker;
        return this;
    }

    public SparseSGD learningRate(double learningRate) {
        this.learningRate = learningRate;
        return this;
    }

    public SparseSGD useBoldDriver(boolean useBoldDriver) {
        this.useBoldDriver = useBoldDriver;
        return this;
    }

    public SparseSGD maxEpochs(int maxEpochs) {
        this.maxEpochs = maxEpochs;
        return this;
    }

    public SparseSGD minPredictedProbablity(double minPredictedProbablity) {
        this.minPredictedProbablity = minPredictedProbablity;
        return this;
    }

    public SparseSGD weightConvergenceThreshold(double weightConvergenceThreshold) {
        this.weightConvergenceThreshold = weightConvergenceThreshold;
        return this;
    }

    public SparseSGD maxGradientNorm(double maxGradientNorm) {
        this.maxGradientNorm = maxGradientNorm;
        return this;
    }

    public SparseSGD learningRateReductionFactor(double learningRateReductionFactor) {
        this.learningRateReductionFactor = learningRateReductionFactor;
        return this;
    }

    public SparseSGD learningRateBoostFactor(double learningRateBoostFactor) {
        this.learningRateBoostFactor = learningRateBoostFactor;
        return this;
    }

    public SparseSGD costConvergenceThreshold(double costConvergenceThreshold) {
        this.costConvergenceThreshold = costConvergenceThreshold;
        return this;
    }

    public SparseSGD minEpochs(int minEpochs) {
        this.minEpochs = minEpochs;
        return this;
    }

    public SparseSGD minibatchSize(int minibatchSize) {
        this.minibatchSize = minibatchSize;
        return this;
    }

    public SparseSGD ridgeRegularizationConstant(final double ridgeRegularizationConstant) {
        this.ridge = ridgeRegularizationConstant;
        return this;
    }

    public SparseSGD lassoRegularizationConstant(final double ridgeRegularizationConstant) {
        this.lasso = ridgeRegularizationConstant;
        return this;
    }


    @Override
    public double[] minimize(final List<SparseClassifierInstance > sparseClassifierInstances, int numRegressors) {
        /** minimizes the cross entropy loss function. NumRegressors includes the bias term.
         */
        executorService = Executors.newFixedThreadPool(executorThreadCount);

        double[] weights = initializeWeights(numRegressors);
        double previousCostFunctionValue = 0;
        double costFunctionValue = computeCrossEntropyCostFunction(sparseClassifierInstances, weights, minPredictedProbablity, ridge, lasso);

        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            logCostFunctionValueAtRegularIntervals(previousCostFunctionValue, costFunctionValue, epoch);
            double[] weightsAtPreviousEpoch = Arrays.copyOf(weights, weights.length);
            for (int miniBatchStartIndex = 0; miniBatchStartIndex < sparseClassifierInstances.size(); miniBatchStartIndex += minibatchSize) {
                final double[] fixedWeights = Arrays.copyOf(weights, weights.length);
                final double[] gradient = new double[weights.length];

                int currentMiniBatchSize = getCurrentMiniBatchSize(minibatchSize, sparseClassifierInstances.size(), miniBatchStartIndex);
                final int[] threadStartAndStopIndices = getThreadStartIndices(miniBatchStartIndex, currentMiniBatchSize, executorThreadCount, minInstancesForParrellization);
                int actualNumThreads = threadStartAndStopIndices.length - 1;
                if (sparseParallelization) {
                    sparseCalculationOfGradient(   sparseClassifierInstances, fixedWeights, gradient, threadStartAndStopIndices, actualNumThreads);
                } else {
                    nonSparseCalculationOfGradient(sparseClassifierInstances, fixedWeights, gradient, threadStartAndStopIndices, actualNumThreads);

                }
                addRegularizationComponentOfTheGradient(weights, gradient, ridge, lasso);
                normalizeTheGradient(currentMiniBatchSize, maxGradientNorm, gradient);

                for (int k = 0; k < weights.length; k++) {
                    weights[k] = weights[k] - gradient[k] * learningRate;
                }
            }
            previousCostFunctionValue = costFunctionValue;
            costFunctionValue = computeCrossEntropyCostFunction(sparseClassifierInstances, weights, minPredictedProbablity, ridge, lasso);
            if (ceaseMinimization(weights, previousCostFunctionValue, epoch, weightsAtPreviousEpoch)) {
                logger.info("breaking after {} epochs with cost {}", epoch + 1, costFunctionValue);
                break;
            }
            adjustLearningRateIfNecessary(previousCostFunctionValue, costFunctionValue);
            Collections.shuffle(sparseClassifierInstances);

        }
        executorService.shutdown();

        return weights;
    }

    private void sparseCalculationOfGradient(final List<? extends SparseClassifierInstance> sparseClassifierInstances, final double[] fixedWeights, double[] gradient, final int[] threadStartAndStopIndices, int actualNumThreads) {
        List<Future<Int2DoubleOpenHashMap>> contributionsToTheGradient = Lists.newArrayListWithCapacity(actualNumThreads);
        for (int i = 0; i < actualNumThreads; i++) {
            final int index = i;
            contributionsToTheGradient.add(executorService.submit(new Callable<Int2DoubleOpenHashMap>() {
                @Override
                public Int2DoubleOpenHashMap call() throws Exception {
                    expectedFractionOfFeaturesToUpdatePerWorker = 1.0;
                   try {
                       Int2DoubleOpenHashMap sparseWorkerContributionToTheGradient = getSparseWorkerContributionToTheGradient(sparseClassifierInstances.subList(threadStartAndStopIndices[index], threadStartAndStopIndices[index + 1]), fixedWeights, expectedFractionOfFeaturesToUpdatePerWorker);
                       return sparseWorkerContributionToTheGradient;

                   } catch (IllegalArgumentException e) {
                       logger.info("what?");
                       throw new RuntimeException(e);
                   }
                }
            }));
        }
        sparseReductionToTheGradient(gradient, contributionsToTheGradient);
    }

    private void nonSparseCalculationOfGradient(final List<? extends SparseClassifierInstance> sparseClassifierInstances, final double[] fixedWeights, double[] gradient, final int[] threadStartAndStopIndices, int actualNumThreads) {
        List<Future<double[]>> contributionsToTheGradient = Lists.newArrayListWithCapacity(actualNumThreads);
        for (int i = 0; i < actualNumThreads; i++) {
            final int index = i;
            contributionsToTheGradient.add(executorService.submit(new Callable<double[]>() {
                @Override
                public double[] call() throws Exception {
                    expectedFractionOfFeaturesToUpdatePerWorker = 1.0;
                    return getWorkerContributionToTheGradient(sparseClassifierInstances.subList(threadStartAndStopIndices[index], threadStartAndStopIndices[index + 1]), fixedWeights);
                }
            }));
        }
        reductionToTheGradient(gradient, contributionsToTheGradient);
    }

    public static int getCurrentMiniBatchSize(int minibatchSize, int totalNumInstances, int miniBatchStartIndex) {
        return Math.min(minibatchSize, totalNumInstances - miniBatchStartIndex);
    }

    public static int[] getThreadStartIndices(int miniBatchStartIndex, int currentMiniBatchSize, int executorThreadCount, int minInstancesForParrallization) {
        int actualNumThreads = executorThreadCount;
        if (currentMiniBatchSize < minInstancesForParrallization) {
            int[] threadStartIndices = new int[2];
            threadStartIndices[0] = miniBatchStartIndex;
            threadStartIndices[1] = miniBatchStartIndex + currentMiniBatchSize;
            return threadStartIndices;
        } else if (currentMiniBatchSize <= executorThreadCount) {
            actualNumThreads = currentMiniBatchSize;
            int[] threadStartIndices = new int[actualNumThreads+1];
            for (int i = 0; i < actualNumThreads; i++) {
                threadStartIndices[i] = miniBatchStartIndex + i;
            }
            threadStartIndices[actualNumThreads] = miniBatchStartIndex + actualNumThreads; //could be put in loop but follow the convention of putting final stop index outside
            return threadStartIndices;
        }


        int[] threadStartIndices = new int[executorThreadCount + 1];

        int lowerSamplesPerThread = currentMiniBatchSize / executorThreadCount;
        int upperSamplesPerThread = currentMiniBatchSize / executorThreadCount + 1;
        int remainder = currentMiniBatchSize % executorThreadCount;
        int currentStartIndex = miniBatchStartIndex;
        for (int i = 0; i < executorThreadCount; i++) {
            threadStartIndices[i] = currentStartIndex;
            if (i >= executorThreadCount - remainder) {
                currentStartIndex += upperSamplesPerThread;
            } else {
                currentStartIndex += lowerSamplesPerThread;
            }
        }
        threadStartIndices[executorThreadCount] = miniBatchStartIndex + currentMiniBatchSize;
        return threadStartIndices;

    }

    private boolean ceaseMinimization(double[] weights, double previousCostFunctionValue, int epoch, double[] weightsAtPreviousEpoch) {
        return epoch > minEpochs && weightsConverged(weights, weightsAtPreviousEpoch, weightConvergenceThreshold)
                && costsConverged(previousCostFunctionValue, previousCostFunctionValue, costConvergenceThreshold);
    }

    public static void addRegularizationComponentOfTheGradient(double[] weights, double[] gradient, double ridge, double lasso) {
        for (int i = 1; i < weights.length; i++) {//start at 1 to skip the bias term
            double lassoDerivative = lasso;
            if (weights[i] < 0.0) {
                lassoDerivative *= -1;
            }
            gradient[i] += ridge * weights[i] + lassoDerivative;
        }
    }

    public static Int2DoubleOpenHashMap getSparseWorkerContributionToTheGradient(List<? extends SparseClassifierInstance> instances, double[] weights, double expectedFractionOfFeaturesToUpdate) {
        Int2DoubleOpenHashMap contributionsToTheGradient = new Int2DoubleOpenHashMap((int) (expectedFractionOfFeaturesToUpdate * weights.length));
        contributionsToTheGradient.defaultReturnValue(0.0);
        for (SparseClassifierInstance instance : instances) {
            sparseUpdateUnnormalizedGradientForInstance(weights, contributionsToTheGradient, instance);
        }
        return contributionsToTheGradient;
    }
    public static void sparseReductionToTheGradient(double[] gradient, List<Future<Int2DoubleOpenHashMap>> contributions) {
        for (Future<Int2DoubleOpenHashMap> contribution : contributions) {
            addSparseContribution(gradient, contribution);
        }
    }

    public static void addSparseContribution(double[] gradient, Future<Int2DoubleOpenHashMap> contributionFuture) {
        try {
            Int2DoubleOpenHashMap contribution = contributionFuture.get();
            for (Int2DoubleMap.Entry entry : contribution.int2DoubleEntrySet()) {
                gradient[entry.getKey()] += entry.getValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static double[] getWorkerContributionToTheGradient(List<? extends SparseClassifierInstance> instances, double[] weights) {
        double[] contributionsToTheGradient = new double[weights.length];
        for (SparseClassifierInstance instance : instances) {
            updateUnnormalizedGradientForInstance(weights, contributionsToTheGradient, instance);
        }
        return contributionsToTheGradient;
    }
    public static void  reductionToTheGradient(double[] gradient, List<Future<double[]>> contributions) {
        for (Future<double[]> contribution : contributions) {
            addContribution(gradient, contribution);
        }
    }

    public static void addContribution(double[] gradient, Future<double[]> contributionFuture) {
        try {
            double[] contribution = contributionFuture.get();
            for (int i = 0; i< gradient.length; i++) {
                gradient[i] += contribution[i];
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private void logCostFunctionValueAtRegularIntervals(double previousCostFunctionValue, double costFunctionValue, int i) {
        if (maxEpochs < 10 || i % (maxEpochs / 10) == 0) {
            logger.info("cost {}, prevCost {}, learning rate {}, before epoch {}", costFunctionValue, previousCostFunctionValue, learningRate, i);
        }
    }

    private void adjustLearningRateIfNecessary(double previousCost, double currentCost) {
        if (useBoldDriver) {
            if (previousCost > currentCost) {
                learningRate = learningRate * learningRateBoostFactor;
            } else {
                learningRate = learningRate * learningRateReductionFactor;
            }
        }
    }

    public static boolean weightsConverged(double[] weights, double[] newWeights, double weightConvergenceThreshold) {
        double meanSquaredDifference = 0;
        double normSquared = 0.0;
        for (int i = 0; i < weights.length; i++) {
            meanSquaredDifference += (weights[i] - newWeights[i]) * (weights[i] - newWeights[i]);
            normSquared += weights[i] * weights[i];
        }
        return Math.sqrt(meanSquaredDifference / normSquared) < weightConvergenceThreshold;
    }

    public static boolean costsConverged(double previousCost, double presentCost, double costConvergenceThreshold) {
        return Math.abs(presentCost - previousCost) / presentCost < costConvergenceThreshold;
    }

    public static double computeCrossEntropyCostFunction(List<? extends SparseClassifierInstance> instances, double[] weights, double minPredictedProbablity, double ridge, double lasso) {
        double cost = 0.0;
        for (SparseClassifierInstance instance : instances) {
            if ((double) instance.getLabel() == 1.0) {
                cost += -cappedlogBase2(probabilityOfThePositiveClass(weights, instance), minPredictedProbablity);
            } else if ((double) instance.getLabel() == 0.0) {
                cost += -cappedlogBase2(probabilityOfTheNegativeClass(weights, instance), minPredictedProbablity);
            }
        }
        cost += getRegularizationCost(weights, ridge, lasso);
        cost /= instances.size();


        return cost;

    }

    public static double probabilityOfTheNegativeClass(double[] weights, SparseClassifierInstance instance) {
        return 1.0 - probabilityOfThePositiveClass(weights, instance);
    }

    public static double probabilityOfThePositiveClass(double[] weights, SparseClassifierInstance instance) {
        return sigmoid(instance.dotProduct(weights));
    }

    public static double getRegularizationCost(double[] weights, double ridge, double lasso) {
        double cost = 0;
        for (int i = 0; i < weights.length; i++) {
            cost += weights[i] * weights[i] * ridge / 2.0 + Math.abs(weights[i]) * lasso;
        }
        return cost;
    }


    public static void normalizeTheGradient(int minibatchSize, double maxGradientNorm, double[] gradient) {
        for (int i = 1; i < gradient.length; i++) {
            gradient[i] /= minibatchSize;
        }
        if (maxGradientNorm != Double.MAX_VALUE) {
            applyMaxGradientNorm(maxGradientNorm, gradient);
        }
    }

    public static void applyMaxGradientNorm(double maxGradientNorm, double[] gradient) {
        double gradientSumOfSquares = 0;
        for (double g : gradient) {
            gradientSumOfSquares += Math.pow(g, 2);
        }
        double gradientNorm = Math.sqrt(gradientSumOfSquares);
        if (gradientNorm > maxGradientNorm) {
            double n = gradientNorm / maxGradientNorm;
            for (int i = 0; i < gradient.length; i++) {
                gradient[i] = gradient[i] / Math.sqrt(n);
            }
        }
    }


    static void sparseUpdateUnnormalizedGradientForInstance(double[] weights, Int2DoubleOpenHashMap contributionsToTheGradient,
                                                            SparseClassifierInstance instance) {
        //could do this with a map for truly sparse instances...but
        double postiveClassProbability = probabilityOfThePositiveClass(weights, instance);


        Pair<int[], double[]> sparseAttributes = instance.getSparseAttributes();
        int[] indices = sparseAttributes.getValue0();
        double[] values = sparseAttributes.getValue1();
        for (int i = 0; i < indices.length; i++) {
            int featureIndex = indices[i];
            contributionsToTheGradient.addTo(featureIndex, gradientContributionOfAFeatureValue((Double) instance.getLabel(), postiveClassProbability, values[i]));
        }
    }

    private static double gradientContributionOfAFeatureValue(double label, double postiveClassProbability, double value) {
        return -(label - postiveClassProbability) * value;
    }

    static void updateUnnormalizedGradientForInstance(double[] weights, double[] contributionsToTheGradient,
                                                            SparseClassifierInstance instance) {
        //could do this with a map for truly sparse instances...but
        double postiveClassProbability = probabilityOfThePositiveClass(weights, instance);
        Pair<int[], double[]> sparseAttributes = instance.getSparseAttributes();
        int[] indices = sparseAttributes.getValue0();
        double[] values = sparseAttributes.getValue1();
        for (int i = 0; i < indices.length; i++) {
            int featureIndex = indices[i];
            contributionsToTheGradient[featureIndex] += gradientContributionOfAFeatureValue((Double) instance.getLabel(), postiveClassProbability, values[i]);
        }
    }


    private double[] initializeWeights(int numFeatures) {
        double[] weights = new double[numFeatures];  //presume normalized
        Random random = new Random();
        for (int i = 0; i < numFeatures; i++) {
            weights[i] = random.nextDouble() * 1.0 - 0.5; //a random number between -0.25 and 0.25
        }
        return weights;
    }


}
