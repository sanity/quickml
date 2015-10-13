package quickml.supervised.classifier.logisticRegression;

import javafx.util.Pair;

import java.util.List;
import java.util.Random;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class SGD implements GradientDescent {
    public double learningRate = 10E-5;
    private int minibatchSize = 1;

    private double convergenceThreshold = 0.001;
    private int epochs = 1;
    private double ridge = 0;
    private double lasso = 0;
    private double maxGradientNorm = 0.01;

    public SGD(double learningRate, int minibatchSize, int epochs) {
        this.learningRate = learningRate;
        this.minibatchSize = minibatchSize;
        this.epochs = epochs;
    }

    public SGD setRidge(double ridge) {
        this.ridge = ridge;
        return this;
    }


    public SGD setLasso(double lasso) {
        this.lasso = lasso;
        return this;
    }

    public SGD setConvergenceThreshold(double convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
        return this;
    }

    public SGD setMaxGradientNorm(double maxGradientNorm) {
        this.maxGradientNorm = maxGradientNorm;
        return this;
    }


    @Override
    public double[] minimize(List<SparseClassifierInstance> sparseClassifierInstances, int numFeatures) {
        double[] weights = initializeWeights(numFeatures);

        for (int i = 0; i < epochs; i++) {
            for (int j = 0; j < sparseClassifierInstances.size(); j++) {
                double[] newWeights = new double[numFeatures];
                int miniBatchStartIndex = j * minibatchSize;
                int maxIndex = Math.max(sparseClassifierInstances.size(), miniBatchStartIndex + minibatchSize);
                List<SparseClassifierInstance> miniBatchInstances = sparseClassifierInstances.subList(miniBatchStartIndex, maxIndex);
                double[] grad = getGradient(miniBatchInstances, weights, numFeatures, minibatchSize, ridge, lasso, maxGradientNorm);
                for (int k = 0; k < weights.length; k++) {
                    newWeights[k] = weights[k] - grad[k] * learningRate;
                }
                if (isConverged(weights, newWeights, convergenceThreshold)) {
                    break;
                }
                weights = newWeights;
            }
        }
        return weights;
    }

    static boolean isConverged(double[] weights, double[] newWeights, double convergenceThreshold) {
        double meanSquaredDifference = 0;
        double normSquared = 0.0;
        for (int i = 0; i < weights.length; i++) {
            meanSquaredDifference += (weights[i] - newWeights[i]) * (weights[i] - newWeights[i]);
            normSquared += weights[i] * weights[i];
        }
        return Math.sqrt(meanSquaredDifference / normSquared) < convergenceThreshold;
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
            double n = gradientNorm/maxGradientNorm;
            for(int i = 0; i < gradient.length; i++) {
                gradient[i] = gradient[i] / Math.sqrt(n);
            }
        }
    }

    static double getSigmoidPreFactor(double[] weights, int minibatchSize, SparseClassifierInstance instance) {
        double dotProduct = instance.dotProduct(weights);
        double sigmoidPreFactor;

        if (instance.getLabel().equals(0.0)) {
            sigmoidPreFactor = -quickml.math.Utils.sigmoid(dotProduct) / minibatchSize;
        } else if (instance.getLabel().equals(1.0)) {
            sigmoidPreFactor = -quickml.math.Utils.sigmoid(-dotProduct) / minibatchSize;
        } else {
            throw new RuntimeException("label must be 1 or 0");
        }
        return sigmoidPreFactor;
    }

    static void updateGradientForInstance(double[] weights, double ridge, double lasso, double[] gradient,
                                                  SparseClassifierInstance instance, int minibatchSize) {

        double sigmoidPreFactor = getSigmoidPreFactor(weights, minibatchSize, instance);

        Pair<int[], double[]> sparseAttributes = instance.getSparseAttributes();
        int[] indices = sparseAttributes.getKey();
        double[] values = sparseAttributes.getValue();

        for (int i = 0; i < indices.length; i++) {
            int featureIndex = indices[i];
            if (weights[featureIndex] < 0.0) {
                lasso *= -1;
            }
            gradient[featureIndex] += values[i] * sigmoidPreFactor + 2 * ridge * weights[featureIndex] + lasso;
        }
    }


    private double[] initializeWeights(int numFeatures) {
        double[] weights = new double[numFeatures];  //presume normalized
        Random random = new Random();
        for (int i = 0; i < numFeatures; i++) {
            weights[i] = random.nextDouble() * 0.5 - 0.25; //a random number between -0.25 and 0.25
        }
        return weights;
    }
}
