package quickml.supervised.regressionModel.LinearRegression2;

/**
 * Created by alexanderhawk on 10/12/15.
 */

import com.google.common.collect.Lists;
import org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionBlock;
import org.ejml.alg.dense.decomposition.chol.CholeskyDecompositionCommon;
import org.ejml.alg.dense.linsol.chol.LinearSolverChol;
import org.ejml.data.D1Matrix64F;
import org.ejml.data.DenseMatrix64F;
import quickml.data.instances.RegressionInstance;
import quickml.data.instances.SparseRegressionInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.logisticRegression.InstanceTransformerUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.ejml.alg.dense.linsol.*;
import static org.ejml.ops.CommonOps.*;

/**
 * Created by alexanderhawk on 10/9/15.
 */


public class SimpleRidgeRegressionBuilder<T extends RegressionInstance> implements PredictiveModelBuilder<LinearModel, RegressionInstance> {
    public static final String MIN_OBSERVATIONS_OF_ATTRIBUTE= "minObservationsOfAttribute";
    public static final String RIDGE_REGULARIZATION_CONSTANT = "ridgeRegularizationConstant";
    public static final String USE_BIAS = "useBias";



    private int minObservationsOfAttribute;
    private double ridgeRegularizationConstant;
    private boolean useBias = true;

    public SimpleRidgeRegressionBuilder<T> minObservationsOfAttribute(int minObservationsOfAttribute) {
        this.minObservationsOfAttribute = minObservationsOfAttribute;
        return this;
    }

    public SimpleRidgeRegressionBuilder<T> ridgeRegularizationConstant(double ridgeConstant) {
        this.ridgeRegularizationConstant = ridgeConstant;
        return this;
    }

    public SimpleRidgeRegressionBuilder<T> useBias(boolean useBias) {
        this.useBias = useBias;
        return this;
    }

    @Override
    public LinearModel buildPredictiveModel(Iterable<RegressionInstance> trainingData) {
        List<RegressionInstance> trainingDataList = Lists.newArrayList(trainingData);
        Map<String, Integer> nameToIndexMap = InstanceTransformerUtils.populateNameToIndexMap(trainingDataList, useBias);
        int numVariables = nameToIndexMap.size();

        double[][] data =          new double[trainingDataList.size()][numVariables];
        double[][] responseArray = new double[trainingDataList.size()][1];
        for (int row = 0; row < trainingDataList.size(); row++) {
            RegressionInstance regressionInstance = trainingDataList.get(row);
            data[row] = SparseRegressionInstance.getArrayOfValues(regressionInstance, nameToIndexMap, useBias);
            responseArray[row][0] = regressionInstance.getLabel();
        }

        DenseMatrix64F dataMatrix = new DenseMatrix64F(data);
        DenseMatrix64F dataMatrixTranspose = getTranspose(dataMatrix);
        DenseMatrix64F symmetricMatrix=getSymmetricMatrix(numVariables, dataMatrix, dataMatrixTranspose);
        DenseMatrix64F response =  new DenseMatrix64F(responseArray);
     //   transpose(response);

        LinearSolverChol linearSolverChol = new LinearSolverChol(new CholeskyDecompositionBlock(numVariables));// new CholeskyDecompositionCommon(true));
        linearSolverChol.setA(symmetricMatrix);

        DenseMatrix64F dataMatrixTransposeTimesResponse = getDataMatrixTransposeTimesResponse(numVariables, dataMatrixTranspose, response);
        DenseMatrix64F coefficients = new DenseMatrix64F(numVariables);
        linearSolverChol.solve(dataMatrixTransposeTimesResponse, coefficients);

        return new LinearModel(coefficients.getData(), nameToIndexMap, useBias);
    }

    private DenseMatrix64F getDataMatrixTransposeTimesResponse(int numVariables, DenseMatrix64F dataMatrixTranspose, DenseMatrix64F response) {
        DenseMatrix64F multipliedResponse = new DenseMatrix64F(numVariables,1);
        mult(dataMatrixTranspose, response, multipliedResponse);
        return multipliedResponse;
    }


    private DenseMatrix64F getSymmetricMatrix(int numVariables, DenseMatrix64F dataMatrix, DenseMatrix64F dataMatrixTranspose) {
        DenseMatrix64F symmetricMatrix = new DenseMatrix64F(numVariables, numVariables);
        mult(dataMatrixTranspose, dataMatrix, symmetricMatrix);
        for (int i = 0; i<dataMatrix.getNumCols(); i++) {
            double diagonalElement = ridgeRegularizationConstant + symmetricMatrix.get(i, i);
            symmetricMatrix.set(i, i, diagonalElement);
        }
        return symmetricMatrix;
    }

    private DenseMatrix64F getTranspose(DenseMatrix64F dataMatrix) {
        DenseMatrix64F dataMatrixTranspose = dataMatrix.copy();
        transpose(dataMatrixTranspose);
        return dataMatrixTranspose;
    }


    @Override
    public void updateBuilderConfig(final Map<String, Serializable> config) {
        if (config.containsKey(MIN_OBSERVATIONS_OF_ATTRIBUTE)) {
            minObservationsOfAttribute((Integer) config.get(MIN_OBSERVATIONS_OF_ATTRIBUTE));
        }
        if (config.containsKey(RIDGE_REGULARIZATION_CONSTANT)) {
            ridgeRegularizationConstant((Double) config.get(RIDGE_REGULARIZATION_CONSTANT));
        }
        if (config.containsKey(USE_BIAS)) {
            useBias((Boolean) config.get(USE_BIAS));
        }

    }
}
