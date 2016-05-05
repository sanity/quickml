package quickml.supervised.crossValidation;

import quickml.data.AttributesMap;
import quickml.data.instances.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.lossfunctions.regressionLossFunctions.RegressionLossFunction;

import java.io.BufferedWriter;
import java.util.List;

/**
 * Created by alexanderhawk on 8/12/15.
 */
public class RegressionLossChecker<PM extends PredictiveModel, T extends Instance<AttributesMap, Double>>  implements LossChecker<PM, T> {
    private RegressionLossFunction lossFunction;

    public RegressionLossChecker(RegressionLossFunction lossFunction) {
        this.lossFunction = lossFunction;
    }

    @Override
    public double calculateLoss(PM predictiveModel, List<T> validationSet) {
        return lossFunction.getLoss(Utils.getRegLabelsPredictionsWeights(predictiveModel, validationSet));
    }

    public double calculateLoss(PM predictiveModel, List<T> validationSet, BufferedWriter bw) {
        return lossFunction.getLoss(Utils.getRegLabelsPredictionsWeights(predictiveModel, validationSet, bw));
    }
}