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
    Map<String, Double> modelCoeficients = new TreeMap<String, Double>();
    double[] modelArray;
    RidgeLinearModel(Map<String, Double> modelCoeficients) {
        this.modelCoeficients = modelCoeficients;
        this.modelArray = new double[modelCoeficients.size()];
    }
    @Override
    public Double predict(Map<String, Double> regressors) {
        double prediction = 0;
        for (String key : regressors.keySet())
            prediction += regressors.get(key)*modelCoeficients.get(key);
        return prediction;
    }
    @Override
    public void dump(Appendable appendable) {
        for (String key : modelCoeficients.keySet()) {
            try {
                appendable.append(key + ":" + modelCoeficients.get(key).toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }
}
