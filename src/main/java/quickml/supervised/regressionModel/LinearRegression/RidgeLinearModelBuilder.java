package quickml.supervised.regressionModel.LinearRegression;

import com.google.common.collect.Iterables;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.javatuples.Pair;
import quickml.data.Instance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.RidgeInstance;

import java.io.Serializable;
import java.util.Map;


/**
 * Created by alexanderhawk on 8/14/14.
 */
public class RidgeLinearModelBuilder implements PredictiveModelBuilder<RidgeLinearModel, RidgeInstance> {

    public static final String REGULARIZATION_CONSTANT = "regularizationConstant";
    public static final String INCLUDE_BIAS_TERM = "includeBiasTerm";

    private double regularizationConstant = 0;
    private Iterable<? extends Instance<double[], Serializable>> trainingData;
    private boolean includeBiasTerm = false;
    private int collumnsInDataMatrix = 0;
    private String[] header;


    @Override
    public void
    updateBuilderConfig(Map<String, Object> cfg) {
        regularizationConstant((Double) cfg.get(REGULARIZATION_CONSTANT));
        if (cfg.containsKey(INCLUDE_BIAS_TERM))
            includeBiasTerm((Boolean) cfg.get(INCLUDE_BIAS_TERM));
    }

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

    public RidgeLinearModelBuilder header(String[] header) {
        this.header = header;
        return this;
    }

    @Override
    public RidgeLinearModel buildPredictiveModel(Iterable<RidgeInstance> trainingData) {

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
                System.out.print(matrix.getEntry(i, j) + " ");
            }
            System.out.print("\n");
        }
    }


    private RealMatrix getIdentiytMatrixTimesRegularizationConstant() {
        RealMatrix identityMatrixTimesRegularizationConstant = new DiagonalMatrix(collumnsInDataMatrix);
        for (int i = 0; i < collumnsInDataMatrix; i++) {
            identityMatrixTimesRegularizationConstant.setEntry(i, i, regularizationConstant);
        }
        return identityMatrixTimesRegularizationConstant;
    }


    private Pair createDataMatrixLabelsPair(Iterable<? extends Instance<double[], Serializable>> trainingData) {
        RealMatrix dataMatrix = new Array2DRowRealMatrix(Iterables.size(trainingData), collumnsInDataMatrix);
        double[] labels = new double[Iterables.size(trainingData)];
        int row = 0;
        for (Instance<double[], Serializable> instance : trainingData) {
            labels[row] = (Double) instance.getLabel();
            double[] attributes = instance.getAttributes();
            int oneIfUsingBiasTerm = 0;
            if (includeBiasTerm) {
                dataMatrix.setEntry(row, 0, 1.0);
                oneIfUsingBiasTerm = 1;
            }
            for (int i = 0; i < attributes.length; i++) {
                dataMatrix.setEntry(row, i + oneIfUsingBiasTerm, attributes[i]);
            }
            row++;
        }
        return new Pair<>(dataMatrix, labels);
    }
}
