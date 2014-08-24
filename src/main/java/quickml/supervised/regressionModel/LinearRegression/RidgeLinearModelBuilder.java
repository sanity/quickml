package quickml.supervised.regressionModel.LinearRegression;

import com.google.common.collect.Iterables;
import org.apache.commons.math3.linear.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.Instance;
import org.javatuples.Pair;
import quickml.supervised.PredictiveModelBuilder;


import java.io.Serializable;


/**
 * Created by alexanderhawk on 8/14/14.
 */
public class RidgeLinearModelBuilder implements PredictiveModelBuilder<double[], RidgeLinearModel> {
    private static final Logger logger = LoggerFactory.getLogger(RidgeLinearModelBuilder.class);

    double regularizationConstant = 0;
    Iterable<? extends Instance<double[]>> trainingData;
    boolean includeBiasTerm = false;
    boolean updatable = false;
    int collumnsInDataMatrix = 0;
    String []header;
    Serializable id;


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

    public RidgeLinearModelBuilder header(String []header) {
        this.header = header;
        return this;
    }

    @Override
    public RidgeLinearModel buildPredictiveModel(Iterable<? extends Instance<double[]>> trainingData) {

        //compute modelCoefficients = (X^t * X + regularizationConstant*IdentityMatrix)^-1 * X^t * labels, where X is the data matrix
        this.trainingData = trainingData;
        collumnsInDataMatrix = (includeBiasTerm) ? header.length + 1 : header.length;

        Pair<RealMatrix, double[]> dataMatrixLabelsPair = createDataMatrixLabelsPair(trainingData);
        RealMatrix dataMatrix = dataMatrixLabelsPair.getValue0();
        double[] labels = dataMatrixLabelsPair.getValue1();
        RealMatrix dataMatrixTranspose = dataMatrix.transpose();
        RealMatrix identityMatrixTimesRegularizationConstant = getIdentiytMatrixTimesRegularizationConstant();

        //log this out
        RealMatrix dataMatrixTransposeTimesDataMatrix = dataMatrixTranspose.multiply(dataMatrix);
        RealMatrix matrixToInvert = dataMatrixTransposeTimesDataMatrix.add(identityMatrixTimesRegularizationConstant);
        RealMatrix invertedMatrix = new SingularValueDecomposition(matrixToInvert).getSolver().getInverse();
        //mult on right by X^t, then by Y
        double[] modelCoefficients = (invertedMatrix.multiply(dataMatrixTranspose)).operate(labels);
        return new RidgeLinearModel(modelCoefficients, header, includeBiasTerm);
    }


    private void printMatrix(RealMatrix matrix) {
        for (int i = 0; i < matrix.getRowDimension(); i++) {
            for (int j = 0; j < matrix.getColumnDimension(); j++) {
                System.out.print(matrix.getEntry(i, j)+ " ");
            }
            System.out.print("\n");
        }
    }

    @Override
    public RidgeLinearModelBuilder updatable(boolean updatable) {
        this.updatable = updatable;
        return this;
    }

    @Override
    public void setID(Serializable id) {
        this.id = id;
    }

    private RealMatrix getIdentiytMatrixTimesRegularizationConstant() {
         RealMatrix identityMatrixTimesRegularizationConstant = new DiagonalMatrix(collumnsInDataMatrix);
        for (int i = 0; i < collumnsInDataMatrix; i++) {
            identityMatrixTimesRegularizationConstant.setEntry(i, i, regularizationConstant);
        }
        return identityMatrixTimesRegularizationConstant;
    }


    private Pair<RealMatrix, double[]> createDataMatrixLabelsPair(Iterable<? extends Instance<double[]>> trainingData) {
        RealMatrix dataMatrix = new Array2DRowRealMatrix(Iterables.size(trainingData), collumnsInDataMatrix);
        double[] labels = new double[Iterables.size(trainingData)];
        int row = 0;
        for (Instance<double[]> instance : trainingData) {
            labels[row] = (Double) instance.getLabel();
            double[] regressors = instance.getRegressors();
            int oneIfUsingBiasTerm = 0;
            if (includeBiasTerm) {
                dataMatrix.setEntry(row, 0, 1.0);
                oneIfUsingBiasTerm = 1;
            }
            for (int i=0; i < regressors.length; i++) {
                dataMatrix.setEntry(row, i+oneIfUsingBiasTerm, regressors[i]);
            }
            row++;
        }
        return new Pair<RealMatrix, double[]>(dataMatrix, labels);
    }
}
