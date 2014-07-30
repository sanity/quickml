package quickdt.crossValidation.crossValLossFunctions;

import com.google.common.base.Preconditions;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;
import quickdt.predictiveModels.Prediction;

/**
 * Created by ian on 2/28/14.
 */
public class MSECrossValLossFunction<Double, I extends AbstractInstance> extends OnlineClassifierCVLossFunction<Double, I> {

    @Override
    public double getLossFromInstance(InstancePredictionPair<Double, I> instancePredictionPair) {
        double prediction = instancePredictionPair.prediction;
        Preconditions.checkArgument(!Double.isNaN(prediction), "Prediction must be a natural number, not NaN");
        Preconditions.checkArgument(!Double.isInfinite(prediction), "Probability must be a natural number, not infinite");

        final double error = ( - probabilityOfCorrectInstance);
        final double errorSquared = error*error*weight;
        return errorSquared;
    }

    @Override
    public String toString() {
        return "MSE: "+ super.totalLoss;
    }
}
