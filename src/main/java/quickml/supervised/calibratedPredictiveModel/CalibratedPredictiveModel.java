package quickml.supervised.calibratedPredictiveModel;
import com.google.common.base.Preconditions;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import java.io.Serializable;


/**
 * Created by alexanderhawk on 3/10/14.
 */

/*
  This class uses the Pool-adjacent violators algorithm to calibrate the probabilities returned by a random forest of probability estimation trees.
*/
public class CalibratedPredictiveModel implements Classifier {
    private static final long serialVersionUID = 8291739965981425742L;
    public PoolAdjacentViolatorsModel pavFunction;
    public Classifier wrappedPredictiveModel;

    public CalibratedPredictiveModel(Classifier wrappedPredictiveModel, PoolAdjacentViolatorsModel PAVFunction) {
        Preconditions.checkArgument(!(wrappedPredictiveModel instanceof CalibratedPredictiveModel));
        this.wrappedPredictiveModel = wrappedPredictiveModel;
        this.pavFunction = PAVFunction;
    }

    // FIXME: This assumes that the second parameter will always be the same.
    public double getProbability(AttributesMap attributes, Serializable label) {
        double rawProbability = wrappedPredictiveModel.getProbability(attributes, label);
        return pavFunction.predict(rawProbability);
    }

    /**
     * Unsupported at this time, will throw UnsupportedOperationException
     *
     * @param attributes
     * @return
     */
    @Override
    public PredictionMap predict(final AttributesMap attributes) {
        PredictionMap predictionMap = wrappedPredictiveModel.predict(attributes);
        for (Serializable prediction : predictionMap.keySet()) {
            predictionMap.put(prediction, pavFunction.predict((Double) prediction));
        }
        return predictionMap;
    }

    @Override
    public void dump(Appendable appendable) {
        wrappedPredictiveModel.dump(appendable);
        pavFunction.dump(appendable);
    }

    @Override
    public Serializable getClassificationByMaxProb(AttributesMap attributes) {
        PredictionMap predictionMap = predict(attributes);
        double maxProb = 0;
        Serializable classification = null;
        for (Serializable prediction : predictionMap.keySet()) {
            double prob = predictionMap.get(prediction);
            if (prob > maxProb) {
                maxProb = prob;
                classification = prediction;
            }
        }
        assert (classification != null);
        return classification;
    }
}


