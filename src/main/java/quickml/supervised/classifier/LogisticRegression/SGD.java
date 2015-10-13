package quickml.supervised.classifier.logisticRegression;

import javafx.util.Pair;

import java.util.List;
import java.util.Random;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public class SGD implements GradientDescent {
    public double learningRate = 10E-5;
    public int minibatchSize = 1;
    public int maxIts;
    double convergenceThreshold = 0.001;
    public int epochs = 1;
    public double ridge = 0;
    public double lasso = 0;
    public double maxDerivative = .001;

    public SGD(double learningRate, int minibatchSize, int maxIts, int epochs) {
        this.learningRate = learningRate;
        this.minibatchSize = minibatchSize;
        this.maxIts = maxIts;
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

    public SGD setMaxDerivative(double maxDerivative) {
        this.maxDerivative = maxDerivative;
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
                double[] grad = getGradient(miniBatchInstances, weights, numFeatures, minibatchSize, ridge, lasso, maxDerivative);
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

    public static boolean isConverged(double[] weights, double[] newWeights, double convergenceThreshold) {
        double meanSquaredDifference = 0;
        double norm = 0.0;
        for (int i = 0; i < weights.length; i++) {
            meanSquaredDifference += (weights[i] - newWeights[i]) * (weights[i] - newWeights[i]);
            norm += weights[i] * weights[i];
        }
        return Math.sqrt(meanSquaredDifference / norm) < convergenceThreshold;
    }


    public static double[] getGradient(List<SparseClassifierInstance> instances, double[] weights, int numFeatures, int minibatchSize, double ridge, double lasso, double maxDerivative) {
        /**expression for the d(CrossEntropyCostFunction)/ d(weight_j): SumOverInstanceOfIndex_i( -label_i *sigmoid(-dotProduct(weights, attributes))*attributeValue_j
         * - (1-label_i) *sigmoid(dotProduct(weights, attributes))*attributeValue_j.
         */
        double[] grad = new double[numFeatures];
        for (SparseClassifierInstance instance : instances) {
            double dotProduct = instance.dotProduct(weights);
            double sigmoidPreFactor = 0.0;

            if (instance.getLabel().equals(0.0)) {
                sigmoidPreFactor = -quickml.math.Utils.sigmoid(dotProduct) / minibatchSize;
            } else if (instance.getLabel().equals(1.0)) {
                sigmoidPreFactor = -quickml.math.Utils.sigmoid(-dotProduct) / minibatchSize;
            } else {
                throw new RuntimeException("label must be 1 or 0");
            }

            Pair<int[], double[]> sparseAttributes = instance.getSparseAttributes();
            int[] indices = sparseAttributes.getKey();
            double[] values = sparseAttributes.getValue();

            for (int i = 0; i < indices.length; i++) {
                int featureIndex = indices[i];
                double lassoDerivative = 0.0;
                if (weights[featureIndex] > 0.0) {
                    lassoDerivative = 1;
                } else if (weights[featureIndex] < 0.0) {
                    lassoDerivative = -1;
                }
                grad[featureIndex] += values[i] * sigmoidPreFactor + 2 * ridge * weights[featureIndex] + lasso * lassoDerivative;
            }
        }
        for (int i = 0; i < grad.length; i++) {
            if (Math.abs(grad[i]) > maxDerivative)
                grad[i] = maxDerivative;
        }
        return grad;
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
