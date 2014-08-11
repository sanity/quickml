package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.base.Preconditions;
import quickdt.predictiveModels.Classifier;
import quickdt.predictiveModels.PredictiveModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by alexanderhawk on 3/10/14.
 */

/*
  This class uses the Pool-adjacent violators algorithm to calibrate the probabilities returned by a random forest of probability estimation trees.

public class CalibratedPredictiveModel extends Classifier {
    private static final long serialVersionUID = 8291739965981425742L;
    public final Calibrator calibrator;
    public final PredictiveModel<Map<String, Serializable>, Map<Serializable, Double>> predictiveModel;
    public final Serializable positiveClassification;

    public CalibratedPredictiveModel (PredictiveModel<Map<String, Serializable>, Map<Serializable, Double>> predictiveModel, Calibrator calibrator, Serializable positiveClassification) {
        Preconditions.checkArgument(!(predictiveModel instanceof CalibratedPredictiveModel));
        this.predictiveModel = predictiveModel;
        this.calibrator = calibrator;
        this.positiveClassification = positiveClassification;
    }

    @Override
    public Map<Serializable, Double> predict(Map<String, Serializable> attributes) {
        Map<Serializable, Double> predictions = predictiveModel.predict(attributes);
        Map<Serializable, Double> calibratedPredictions = new HashMap<>();
        for(Map.Entry<Serializable, Double> prediction : predictions.entrySet()) {
            calibratedPredictions.put(prediction.getKey(), calibrator.correct(prediction.getValue()));
        }
        return calibratedPredictions;
    }


    @Override
    public void dump(Appendable appendable) {
        predictiveModel.dump(appendable);
    }
}
*/