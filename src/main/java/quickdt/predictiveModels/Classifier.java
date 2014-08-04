package quickdt.predictiveModels;

import quickdt.data.Attributes;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class Classifier extends AbstractPredictiveModel<ClassifierPrediction> implements PredictiveModel<ClassifierPrediction>  {
    public double getProbability(Attributes attributes, Serializable classification) {
        return predict(attributes).getPrediction().get(classification);
    }
    public abstract ClassifierPrediction predict(Attributes attributes);

    public Serializable getClassificationByMaxProb(Attributes attributes) {
        Map<Serializable, Double> predictions = predict(attributes).getPrediction();
        Serializable mostProbableClass = null;
        double probabilityOfMostProbableClass = 0;
        for (Serializable key : predictions.keySet()) {
            if (predictions.get(key).doubleValue() > probabilityOfMostProbableClass) {
                mostProbableClass = key;
                probabilityOfMostProbableClass = predictions.get(key).doubleValue();
            }
        }
        return mostProbableClass;
    }
}
