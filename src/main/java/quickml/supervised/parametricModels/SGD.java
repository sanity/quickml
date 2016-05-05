package quickml.supervised.parametricModels;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.instances.Instance;
import quickml.supervised.classifier.logisticRegression.GradientDescent;

import java.io.Serializable;
import java.util.*;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class SGD<T extends Instance> implements GradientDescent<T> {


    public static final Logger logger = LoggerFactory.getLogger(SGD.class);
    public static final String LEARNING_RATE = "learningRate";
    public static final String USE_BOLD_DRIVER = "useBoldDriver";

    public static final String MAX_EPOCHS = "maxEpochs";
    public static final String MIN_EPOCHS = "minEpochs";
    public static final String MINI_BATCH_SIZE = "miniBatchSize";
    public static final String COST_CONVERGENCE_THRESHOLD = "costConvergenceThreshold";
    public static final String LEARNING_RATE_BOOST_FACTOR = "learningRateBoostFactor";
    public static final String LEARNING_RATE_REDUCTION_FACTOR = "learningRateReductionFactor";
    public static final String WEIGHT_CONVERGENCE_THRESHOLD = "weightConvergenceThreshold";
    public static final String MIN_PREDICTED_PROBABILITY = "minPredictedProbablity";
    public static final String OPTIMIZABLE_COST_FUNCTION = "optimizableCostFunction";


    //training hyper-params
    private int minibatchSize = 1;
    private int maxEpochs = 8;
    private int minEpochs = 3;

    private double weightConvergenceThreshold = 0.001;
    private double costConvergenceThreshold = 0.001;

    private double learningRate = 10E-5;
    private double minPredictedProbablity = 10E-6;
    private double learningRateReductionFactor = 0.5;
    private double learningRateBoostFactor = 1.07;
    private boolean useBoldDriver = false;

    private OptimizableCostFunction<T> optimizableCostFunction;


    public SGD() {
    }

    public void updateBuilderConfig(final Map<String, Serializable> config) {
        optimizableCostFunction.updateBuilderConfig(config);

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
        if (config.containsKey(WEIGHT_CONVERGENCE_THRESHOLD)) {
            weightConvergenceThreshold((Double) config.get(WEIGHT_CONVERGENCE_THRESHOLD));
        }
        if (config.containsKey(MIN_PREDICTED_PROBABILITY)) {
            minPredictedProbablity((Double) config.get(MIN_PREDICTED_PROBABILITY));
        }
        if (config.containsKey(OPTIMIZABLE_COST_FUNCTION)) {
            optimizableCostFunction((OptimizableCostFunction<T> ) config.get(OPTIMIZABLE_COST_FUNCTION));
        }

    }

    public OptimizableCostFunction<T> getOptimizableCostFunction() {
        return optimizableCostFunction;
    }

    public SGD optimizableCostFunction(OptimizableCostFunction<T> optimizableCostFunction) {
        this.optimizableCostFunction = optimizableCostFunction;
        return this;
    }




    public SGD learningRate(double learningRate) {
        this.learningRate = learningRate;
        return this;
    }

    public SGD useBoldDriver(boolean useBoldDriver) {
        this.useBoldDriver = useBoldDriver;
        return this;
    }

    public SGD maxEpochs(int maxEpochs) {
        this.maxEpochs = maxEpochs;
        return this;
    }

    public SGD minPredictedProbablity(double minPredictedProbablity) {
        this.minPredictedProbablity = minPredictedProbablity;
        return this;
    }

    public SGD weightConvergenceThreshold(double weightConvergenceThreshold) {
        this.weightConvergenceThreshold = weightConvergenceThreshold;
        return this;
    }


    public SGD learningRateReductionFactor(double learningRateReductionFactor) {
        this.learningRateReductionFactor = learningRateReductionFactor;
        return this;
    }

    public SGD learningRateBoostFactor(double learningRateBoostFactor) {
        this.learningRateBoostFactor = learningRateBoostFactor;
        return this;
    }

    public SGD costConvergenceThreshold(double costConvergenceThreshold) {
        this.costConvergenceThreshold = costConvergenceThreshold;
        return this;
    }

    public SGD minEpochs(int minEpochs) {
        this.minEpochs = minEpochs;
        return this;
    }

    public SGD minibatchSize(int minibatchSize) {
        this.minibatchSize = minibatchSize;
        return this;
    }


    @Override
    public double[] minimize(final List<T > instances, int numRegressors) {
        /** minimizes the cross entropy loss function. NumRegressors includes the bias term.
         */

        double[] weights = initializeWeights(numRegressors);
        double previousCostFunctionValue = 0;
        double costFunctionValue = optimizableCostFunction.computeCost(instances, weights, minPredictedProbablity);

        for (int epoch = 0; epoch < maxEpochs; epoch++) {
            logCostFunctionValueAtRegularIntervals(previousCostFunctionValue, costFunctionValue, epoch);
            double[] weightsAtPreviousEpoch = Arrays.copyOf(weights, weights.length);
            for (int miniBatchStartIndex = 0; miniBatchStartIndex < instances.size(); miniBatchStartIndex += minibatchSize) {
                final double[] fixedWeights = Arrays.copyOf(weights, weights.length);
                final double[] gradient = new double[weights.length];

                int currentMiniBatchSize = getCurrentMiniBatchSize(minibatchSize, instances.size(), miniBatchStartIndex);

                List<T> instancesForBatch = instances.subList(miniBatchStartIndex, currentMiniBatchSize);
                optimizableCostFunction.updateGradient(instancesForBatch, fixedWeights, gradient);


                for (int k = 0; k < weights.length; k++) {
                    weights[k] = weights[k] - gradient[k] * learningRate;
                }
            }
            previousCostFunctionValue = costFunctionValue;
            costFunctionValue = optimizableCostFunction.computeCost(instances, weights, minPredictedProbablity);
            if (ceaseMinimization(weights, previousCostFunctionValue, epoch, weightsAtPreviousEpoch)) {
                logger.info("breaking after {} epochs with cost {}", epoch + 1, costFunctionValue);
                break;
            }
            adjustLearningRateIfNecessary(previousCostFunctionValue, costFunctionValue);
            Collections.shuffle(instances);

        }
        optimizableCostFunction.shutdown();

        return weights;
    }



    public static int getCurrentMiniBatchSize(int minibatchSize, int totalNumInstances, int miniBatchStartIndex) {
        return Math.min(minibatchSize, totalNumInstances - miniBatchStartIndex);
    }



    private boolean ceaseMinimization(double[] weights, double previousCostFunctionValue, int epoch, double[] weightsAtPreviousEpoch) {
        return epoch > minEpochs && weightsConverged(weights, weightsAtPreviousEpoch, weightConvergenceThreshold)
                && costsConverged(previousCostFunctionValue, previousCostFunctionValue, costConvergenceThreshold);
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

    private double[] initializeWeights(int numFeatures) {
        double[] weights = new double[numFeatures];  //presume normalized
        Random random = new Random();
        for (int i = 0; i < numFeatures; i++) {
            weights[i] = random.nextDouble() * 1.0 - 0.5; //a random number between -0.25 and 0.25
        }
        return weights;
    }


}
