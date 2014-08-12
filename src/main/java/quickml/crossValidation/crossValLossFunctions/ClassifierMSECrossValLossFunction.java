package quickml.crossValidation.crossValLossFunctions;

import com.google.common.base.Preconditions;

/**
 * Created by ian on 2/28/14.
 */
public class ClassifierMSECrossValLossFunction extends OnlineClassifierCVLossFunction {

    @Override
    public double getLossFromInstance(double probabilityOfCorrectInstance, double weight) {
        Preconditions.checkArgument(!Double.isNaN(probabilityOfCorrectInstance), "Prediction must be a natural number, not NaN");
        Preconditions.checkArgument(!Double.isInfinite(probabilityOfCorrectInstance), "Probability must be a natural number, not infinite");
        final double error = (1.0-probabilityOfCorrectInstance);
        final double errorSquared = error * error * weight;
        return errorSquared;
    }

    @Override
    public String toString() {
        return "MSE: " + super.totalLoss;
    }
}
