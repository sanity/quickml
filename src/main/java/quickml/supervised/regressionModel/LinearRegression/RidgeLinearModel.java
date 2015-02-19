package quickml.supervised.regressionModel.LinearRegression;

import quickml.supervised.regressionModel.MultiVariableRealValuedFunction;

import java.io.IOException;
import java.util.Set;

/**
 * Created by alexanderhawk on 8/12/14.
 */
public class RidgeLinearModel implements MultiVariableRealValuedFunction {
    double []modelCoeficients; //TreeMap<String, Double>();
    String []modelHeader;
    boolean useBias = true;

    RidgeLinearModel(double []modelCoeficients, String []modelHeader, boolean useBias) {
        this.modelCoeficients = modelCoeficients;
        this.modelHeader = modelHeader;
        this.useBias = useBias;
    }

    RidgeLinearModel(double []modelCoeficients, boolean useBias) {
        this.useBias = useBias;
        this.modelCoeficients = modelCoeficients;
        modelHeader = new String[modelCoeficients.length];
        for (int i = 0; i<modelCoeficients.length; i++) {
            modelHeader[i] = Integer.valueOf(i).toString();
        }
    }

    @Override
    public Double predict(double[] attributes) {
        double prediction = 0;
        int oneIfUsingBias = 0;
        if (useBias) {
            prediction += modelCoeficients[0];
            oneIfUsingBias = 1;
        }
        for (int i=0; i< attributes.length; i++) {
            prediction += attributes[i] * modelCoeficients[i + oneIfUsingBias];
        }
        return prediction;
    }

    @Override
    public Double predictWithoutAttributes(double[] attributes, Set<String> attributesToIgnore) {
        boolean currentlyUnsupported = true;
        if(currentlyUnsupported) {
            throw new UnsupportedOperationException("this operation is currently unsupported");
        }
        return Double.valueOf(0.0);
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

    public double[] getModelCoefficients(){
        return modelCoeficients;
    }

}
