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
  This class uses the Pool-adjacent violators algorithm to calibrate the probabilities returned by a binary classifier, where postive
  classifications have a label of 1.0, and negative classifications have a label 0.0.
*/
public class CalibratedPredictiveModel implements Classifier {
    private static final long serialVersionUID = 8291739965981425742L;
    public PoolAdjacentViolatorsModel pavFunction;
    public Classifier wrappedPredictiveModel;

    public CalibratedPredictiveModel(Classifier wrappedPredictiveModel, PoolAdjacentViolatorsModel PAVFunction) {
        this.wrappedPredictiveModel = wrappedPredictiveModel;
        this.pavFunction = PAVFunction;
    }

    public double getProbability(AttributesMap attributes, Serializable label) {
        double rawProbability = wrappedPredictiveModel.getProbability(attributes, label);
        return pavFunction.predict(rawProbability);
    }


    @Override
    public PredictionMap predict(final AttributesMap attributes) {
        PredictionMap predictionMap = wrappedPredictiveModel.predict(attributes);
        double positiveClassProb =  pavFunction.predict(wrappedPredictiveModel.getProbability(attributes, 1.0));
        predictionMap.put(Double.valueOf(1.0), positiveClassProb);
        predictionMap.put(Double.valueOf(0.0), 1.0 - positiveClassProb);

        return predictionMap;
    }

    @Override
    public void dump(Appendable appendable) {
        //dump the defining information of the wrapped predictive model
        wrappedPredictiveModel.dump(appendable);
        //dump the calibartion set of the PAV function.
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
        if (classification == null)
            throw new RuntimeException("unable to make a classification");

        return classification;
    }
}


