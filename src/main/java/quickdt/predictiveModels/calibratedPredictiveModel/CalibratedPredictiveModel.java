package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.base.Preconditions;
import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.wrappedPredictiveModel.WrappedPredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;


/**
 * Created by alexanderhawk on 3/10/14.
 */

/*
  This class uses the Pool-adjacent violators algorithm to calibrate the probabilities returned by a random forest of probability estimation trees.
*/
public class CalibratedPredictiveModel extends WrappedPredictiveModel {
    private static final long serialVersionUID = 8291739965981425742L;
    public Calibrator calibrator;

    public CalibratedPredictiveModel (PredictiveModel predictiveModel, Calibrator calibrator) {
        super(predictiveModel);
        Preconditions.checkArgument(!(predictiveModel instanceof CalibratedPredictiveModel));
        this.calibrator = calibrator;
    }

    public double getProbability(Attributes attributes, Serializable classification) {
        double rawProbability = predictiveModel.getProbability(attributes, classification);
        return calibrator.correct(rawProbability);
    }
}
