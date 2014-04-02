package quickdt.calibratedPredictiveModel;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;
import com.google.common.base.Preconditions;


/**
 * Created by alexanderhawk on 3/10/14.
 */

/*
  This class uses the Pool-adjacent violators algorithm to calibrate the probabilities returned by a random forest of probability estimation trees.
*/
public class CalibratedPredictiveModel implements PredictiveModel {
    private static final long serialVersionUID = 8291739965981425742L;
    Calibrator calibrator;
    PredictiveModel predictiveModel;
    int binsInCalibrator = 20;

    public CalibratedPredictiveModel (PredictiveModel predictiveModel, Calibrator calibrator) {
        Preconditions.checkArgument(!(predictiveModel instanceof CalibratedPredictiveModel));
        this.predictiveModel = predictiveModel;
        this.calibrator = calibrator;
    }

    public double getProbability(Attributes attributes, Serializable classification) {
        double rawProbability = predictiveModel.getProbability(attributes, classification);
        double probability = calibrator.correct(rawProbability);
        return probability;
    }


    public void dump(PrintStream printStream) {
        predictiveModel.dump(printStream);
    }

    public Serializable getClassificationByMaxProb(Attributes attributes) {
        return predictiveModel.getClassificationByMaxProb(attributes);
    }
}
