package quickml.supervised.classifier.logRegression;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

import static quickml.MathUtils.logBase2WithMaxError;
import static quickml.MathUtils.sigmoid;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class SGD implements GradientDescent {
    public  static final String RIDGE = "ridge";
    public  static final String LASSO = "lasso";

    public static final Logger logger = LoggerFactory.getLogger(SGD.class);
    public static final String LEARNING_RATE = "learningRate";
    public static final String USE_BOLD_DRIVER = "useBoldDriver";

    public static final String MAX_EPOCHS = "maxEpochs";
    public static final String MIN_EPOCHS = "minEpochs";
    public static final String MINI_BATCH_SIZE = "miniBatchSize";
    public static final String COST_CONVERGENCE_THRESHOLD = "costConvergenceThreshold";
    public static final String LEARNING_RATE_BOOST_FACTOR = "learningRateBoostFactor";
    public static final String LEARNING_RATE_REDUCTION_FACTOR= "learningRateReductionFactor";
    public static final String MAX_GRADIENT_NORM = "maxGradientNorm";
    public static final String WEIGHT_CONVERGENCE_THRESHOLD = "weightConvergenceThreshold";
    public static final String MIN_PREDICTED_PROBABILITY= "minPredictedProbablity";

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
    private double maxGradientNorm = 0.01;
    private double minPredictedProbablity = 10E-6;
    private double learningRateReductionFactor = 0.5;
    private double learningRateBoostFactor = 1.1;
    private boolean useBoldDriver = true;

    public SGD() {
    }

    public void updateBuilderConfig(final Map<String, Serializable> config) {
        if (config.containsKey(LASSO)) {
            ridgeRegularizationConstant((Double) config.get(LASSO));
        }
        if (config.containsKey(RIDGE)) {
            lassoRegularizationConstant((Double) config.get(RIDGE));
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

    public SGD maxGradientNorm(double maxGradientNorm) {
        this.maxGradientNorm = maxGradientNorm;
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

    public SGD ridgeRegularizationConstant(final double ridgeRegularizationConstant) {
        this.ridge = ridgeRegularizationConstant;
        return this;
    }

    public SGD lassoRegularizationConstant(final double ridgeRegularizationConstant) {
        this.lasso = ridgeRegularizationConstant;
        return this;
    }


    @Override
    public double[] minimize(List<SparseClassifierInstance> sparseClassifierInstances, int numFeatures) {
        double[] weights = initializeWeights(numFeatures);
        double previousCostFunctionValue = 0;
        double costFunctionValue = computeCostFunction(sparseClassifierInstances, weights, minPredictedProbablity);

        for (int i = 0; i < maxEpochs; i++) {
            if (i % (maxEpochs / 10) == 0) {
                logger.info("cost {}, prevCost {}, learning rate {}, before epoch {}", costFunctionValue, previousCostFunctionValue, learningRate, i);

            }
            double[] weightsAtPreviousEpoch = Arrays.copyOf(weights, weights.length);
            for (int j = 0; j < sparseClassifierInstances.size(); j += minibatchSize) {
                double[] newWeights = new double[numFeatures];
                int miniBatchStartIndex = j;
                int maxIndex = Math.max(sparseClassifierInstances.size(), miniBatchStartIndex + minibatchSize);
                List<SparseClassifierInstance> miniBatchInstances = sparseClassifierInstances.subList(miniBatchStartIndex, maxIndex);
                double[] grad = getGradient(miniBatchInstances, weights, numFeatures, minibatchSize, ridge, lasso, maxGradientNorm);
                for (int k = 0; k < weights.length; k++) {
                    newWeights[k] = weights[k] - grad[k] * learningRate;
                }
                weights = newWeights;
            }
            previousCostFunctionValue = costFunctionValue;
            costFunctionValue = computeCostFunction(sparseClassifierInstances, weights, minPredictedProbablity);
            if (i > minEpochs
                    && weightsConverged(weights, weightsAtPreviousEpoch, weightConvergenceThreshold)
                    && costsConverged(previousCostFunctionValue, previousCostFunctionValue, costConvergenceThreshold)) {
                logger.info("breaking after {} epochs with cost {}", i + 1, costFunctionValue);
                break;
            }
            if (useBoldDriver) {
                learningRate = adjustLearningRateWithBoldDriver(previousCostFunctionValue, costFunctionValue);
            }
            Collections.shuffle(sparseClassifierInstances);

        }
        return weights;
    }

    private double adjustLearningRateWithBoldDriver(double previousCost, double currentCost) {
//       return learningRate;
        if (previousCost > currentCost) {
            return learningRate * learningRateBoostFactor;
        } else {
            return learningRate * learningRateReductionFactor;
        }
    }

    static boolean weightsConverged(double[] weights, double[] newWeights, double weightConvergenceThreshold) {
        double meanSquaredDifference = 0;
        double normSquared = 0.0;
        for (int i = 0; i < weights.length; i++) {
            meanSquaredDifference += (weights[i] - newWeights[i]) * (weights[i] - newWeights[i]);
            normSquared += weights[i] * weights[i];
        }
        return Math.sqrt(meanSquaredDifference / normSquared) < weightConvergenceThreshold;
    }

    static boolean costsConverged(double previousCost, double presentCost, double costConvergenceThreshold) {
        return Math.abs(presentCost - previousCost) / presentCost < costConvergenceThreshold;
    }

    public static double computeCostFunction(List<SparseClassifierInstance> instances, double weights[], double minPredictedProbablity) {
        double cost = 0.0;
        for (SparseClassifierInstance instance : instances) {
            if ((double) instance.getLabel() == 1.0) {
                cost += -logBase2WithMaxError(sigmoid(instance.dotProduct(weights)), minPredictedProbablity);
            } else if ((double) instance.getLabel() == 0.0) {
                cost += -logBase2WithMaxError(1.0 - sigmoid(instance.dotProduct(weights)), minPredictedProbablity);
            }
        }
        return cost;

    }

    static double[] getGradient(List<SparseClassifierInstance> instances, double[] weights, int numFeatures,
                                int minibatchSize, double ridge, double lasso, double maxGradientNorm) {
        /**expression for the d(CrossEntropyCostFunction)/ d(weight_j): SumOverInstanceOfIndex_i( -label_i *sigmoid(-dotProduct(weights, attributes))*attributeValue_j
         * - (1-label_i) *sigmoid(dotProduct(weights, attributes))*attributeValue_j.
         */
        double[] gradient = new double[numFeatures];
        for (SparseClassifierInstance instance : instances) {
            updateGradientForInstance(weights, ridge, lasso, gradient, instance, minibatchSize);
        }
        for (int i = 0; i < numFeatures; i++) {
            gradient[i] /= minibatchSize;
        }

        applyMaxGradientNorm(maxGradientNorm, gradient);
        return gradient;
    }

    static void applyMaxGradientNorm(double maxGradientNorm, double[] gradient) {
        double gradientSum = 0;
        for (double g : gradient) {
            gradientSum += Math.pow(g, 2);
        }
        double gradientNorm = Math.sqrt(gradientSum);
        if (gradientNorm > maxGradientNorm) {
            double n = gradientNorm / maxGradientNorm;
            for (int i = 0; i < gradient.length; i++) {
                gradient[i] = gradient[i] / Math.sqrt(n);
            }
        }
    }

    static double getProbabilityOfThePositiveClass(double[] weights, SparseClassifierInstance instance) {
        double dotProduct = instance.dotProduct(weights);
        return sigmoid(-dotProduct);
    }

    static void updateGradientForInstance(double[] weights, double ridge, double lasso, double[] gradient,
                                          SparseClassifierInstance instance, int minibatchSize) {

        double postiveClassProbability = getProbabilityOfThePositiveClass(weights, instance);

        Pair<int[], double[]> sparseAttributes = instance.getSparseAttributes();
        int[] indices = sparseAttributes.getKey();
        double[] values = sparseAttributes.getValue();

        for (int i = 0; i < indices.length; i++) {
            int featureIndex = indices[i];
            if (weights[featureIndex] < 0.0) {
                lasso *= -1;
            }
            gradient[featureIndex] += ((double) instance.getLabel() - postiveClassProbability) * values[i] + 2 * ridge * weights[featureIndex] + lasso;
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
