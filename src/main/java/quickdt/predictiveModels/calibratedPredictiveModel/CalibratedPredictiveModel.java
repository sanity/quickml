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
public class CalibratedPredictiveModel implements PredictiveModel<Object> {
    private static final long serialVersionUID = 8291739965981425742L;
    public final Calibrator calibrator;
    public final PredictiveModel<Object> predictiveModel;
    public final Serializable positiveClassification;

    public CalibratedPredictiveModel (PredictiveModel<Object> predictiveModel, Calibrator calibrator, Serializable positiveClassification) {
        Preconditions.checkArgument(!(predictiveModel instanceof CalibratedPredictiveModel));
        this.predictiveModel = predictiveModel;
        this.calibrator = calibrator;
        this.positiveClassification = positiveClassification;
    }

    public double getProbability(Attributes attributes, Serializable classification) {
        double rawProbability = predictiveModel.getProbability(attributes, positiveClassification);
        double corrected = calibrator.correct(rawProbability);
        if (classification.equals(positiveClassification)) {
            return corrected;
        } else {
            return 1.0 - corrected;
        }
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
