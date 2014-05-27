package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.base.Preconditions;
import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;


/**
 * Created by alexanderhawk on 3/10/14.
 */

/*
  This class uses the Pool-adjacent violators algorithm to calibrate the probabilities returned by a random forest of probability estimation trees.
*/
public class CalibratedPredictiveModel implements PredictiveModel {
    private static final long serialVersionUID = 8291739965981425742L;
    public Calibrator calibrator;
    public PredictiveModel predictiveModel;

    public CalibratedPredictiveModel (PredictiveModel predictiveModel, Calibrator calibrator) {
        Preconditions.checkArgument(!(predictiveModel instanceof CalibratedPredictiveModel));
        this.predictiveModel = predictiveModel;
        this.calibrator = calibrator;
    }

    // FIXME: This assumes that the second parameter will always be the same.
    public double getProbability(Attributes attributes, Serializable classification) {
        double rawProbability = predictiveModel.getProbability(attributes, classification);
        return calibrator.correct(rawProbability);
    }

    /**
     * Unsupported at this time, will throw UnsupportedOperationException
     * @param attributes
     * @return
     */
    @Override
    public Map<Serializable, Double> getProbabilitiesByClassification(final Attributes attributes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dump(PrintStream printStream) {
        predictiveModel.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(Attributes attributes) {
        return predictiveModel.getClassificationByMaxProb(attributes);
    }
}
