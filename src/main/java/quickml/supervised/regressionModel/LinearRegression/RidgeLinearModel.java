package quickml.supervised.regressionModel.LinearRegression;

import quickml.data.Instance;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.supervised.regressionModel.MultiVariableRealValuedFunction;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by alexanderhawk on 8/12/14.
 */
public class RidgeLinearModel extends MultiVariableRealValuedFunction {
    double []modelCoeficients; //TreeMap<String, Double>();
    String []modelHeader;

    RidgeLinearModel(double []modelCoeficients, String []modelHeader) {
        this.modelCoeficients = modelCoeficients;
        this.modelHeader = modelHeader;
    }

    RidgeLinearModel(double []modelCoeficients) {
        this.modelCoeficients = modelCoeficients;
        modelHeader = new String[modelCoeficients.length];
        for (int i = 0; i<modelCoeficients.length; i++) {
            modelHeader[i] = Integer.valueOf(i).toString();
        }
    }

    @Override
    public Double predict(double []regressors) {
        double prediction = 0;
        for (int i = 0; i< regressors.length; i++) {
            prediction += regressors[i] * modelCoeficients[i];
        }
        return prediction;
    }
    @Override
    public void dump(Appendable appendable) {
        for (int i = 0; i < modelCoeficients.length; i++) {
            try {
                appendable.append(modelHeader[i] + ":" + modelCoeficients[i] + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
}
