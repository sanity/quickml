package quickml.supervised.regressionModel.LinearRegression;

import com.google.common.collect.Iterables;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.Instance;
import org.javatuples.Pair;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 8/14/14.
 */
public class RidgeLinearModelBuilder {
    private static final Logger logger = LoggerFactory.getLogger(RidgeLinearModelBuilder.class);

    double regularizationConstant = 0;
    Iterable<Instance<Map<String, Double>>> trainingData;
    boolean includeBiasTerm = false;
    int collumnsInDataMatrix = 0;

    public RidgeLinearModelBuilder() {
    }

    public RidgeLinearModelBuilder regularizationConstant(double regularizationConstant) {
        this.regularizationConstant = regularizationConstant;
        return this;
    }

    public RidgeLinearModelBuilder includeBiasTerm(boolean includeBiasTerm) {
        this.includeBiasTerm = includeBiasTerm;
        return this;
    }

    @Override
    public RidgeLinearModel buildPredictiveModel(Iterable<Instance<Map<String, Double>>> trainingData) {
        //compute modelCoefficients = (X^t * X + regularizationConstant*IdentityMatrix)^-1 * X^t * labels, where X is the data matrix

        this.trainingData = trainingData;
        Pair<HashMap<Integer, String>, HashMap<String, Integer>> pairOfMapsBetweenKeysAndIndices = createMapsBetweenIndicesAndKeys(trainingData);
        HashMap<Integer, String> mapOfIndicesToKeys = pairOfMapsBetweenKeysAndIndices.getValue0();
        HashMap<String, Integer> mapOfKeysToIndices = pairOfMapsBetweenKeysAndIndices.getValue1();
        collumnsInDataMatrix = (includeBiasTerm) ? mapOfIndicesToKeys.size()+1 : mapOfIndicesToKeys.size();

        Pair<RealMatrix, double[]> dataMatrixLabelsPair = createDataMatrixLabelsPair(trainingData, mapOfKeysToIndices);
        RealMatrix dataMatrix = dataMatrixLabelsPair.getValue0();
        double[] labels = dataMatrixLabelsPair.getValue1();

        RealMatrix dataMatrixTranspose = dataMatrix.transpose();
        RealMatrix identityMatrixTimesRegularizationConstant = getIdentiytMatrixTimesRegularizationConstant();

        //log this out
        RealMatrix dataMatrixTransposeTimesDataMatrix = dataMatrixTranspose.multiply(dataMatrix);
        RealMatrix matrixToInvert = dataMatrixTransposeTimesDataMatrix.add(identityMatrixTimesRegularizationConstant);
        RealMatrix invertedMatrix = matrixToInvert.psudoInverse();
        //mult on right by X^t, then by Y
        double[] modelCoefficients = (invertedMatrix.multiply(dataMatrixTranspose)).operate(labels);
        HashMap<String, Double> modelCoefficientsMap = new HashMap<>();
        for (int j = 0; j < modelCoefficients.length; j++)
            modelCoefficientsMap.put(mapOfIndicesToKeys.get(j), modelCoefficients[j]);

        return new RidgeLinearModel(modelCoefficientsMap);
    }

    private RealMatrix getIdentiytMatrixTimesRegularizationConstant() {
        RealMatrix identityMatrixTimesRegularizationConstant = new DiagonalMatrix(collumnsInDataMatrix);
        for (int i = 0; i < collumnsInDataMatrix; i++) {
            identityMatrixTimesRegularizationConstant.setEntry(i, i, regularizationConstant);
        }
        return identityMatrixTimesRegularizationConstant;
    }

    private Pair<HashMap<Integer, String>, HashMap<String, Integer>> createMapsBetweenIndicesAndKeys(Iterable<Instance<Map<String, Double>>> trainingData) {
        HashMap<Integer, String> indicesToKeys = new HashMap<>();
        HashMap<String, Integer> keysToIndices = new HashMap<>();

        Set<String> regressorKeySet = trainingData.iterator().next().getRegressors().keySet();
        int i = (includeBiasTerm) ? 1 : 0;
        for (String key : regressorKeySet) {
            indicesToKeys.put(i, key);
            keysToIndices.put(key, i);
            i++;
        }
        return new Pair<HashMap<Integer, String>, HashMap<String, Integer>>(indicesToKeys, keysToIndices);
    }

    private Pair<RealMatrix, double[]> createDataMatrixLabelsPair(Iterable<Instance<Map<String, Double>>> trainingData, Map<String, Integer> mapOfKeysToIndices) {
        RealMatrix dataMatrix = new Array2DRowRealMatrix(Iterables.size(trainingData), collumnsInDataMatrix);
        double[] labels = new double[Iterables.size(trainingData)];
        int row = 0;
        for (Instance<Map<String, Double>> instance : trainingData) {
            labels[row] = (Double) instance.getLabel();
            Map<String, Double> regressors = instance.getRegressors();
            for (String key : regressors.keySet()) {
                int col = mapOfKeysToIndices.get(key);
                double entry = instance.getRegressors().get(key);
                dataMatrix.setEntry(row, col, entry);
            }
            if (includeBiasTerm) {
                dataMatrix.setEntry(row, 0, 1.0);
            }
            row++;
        }
        return new Pair<RealMatrix, double[]>(dataMatrix, labels);
    }

}
