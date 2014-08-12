package quickdt.predictiveModels;

import quickdt.data.MapWithDefaultOfZero;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class Classifier extends AbstractPredictiveModel<Map<String, Serializable>, MapWithDefaultOfZero> implements PredictiveModel<Map<String, Serializable>, MapWithDefaultOfZero>  {

    public double getProbability(Map<String, Serializable> attributes, Serializable classification) {
        return predict(attributes).get(classification);
    }
    public abstract MapWithDefaultOfZero predict(Map<String, Serializable> attributes);

    public Serializable getClassificationByMaxProb(Map<String, Serializable> attributes) {
        MapWithDefaultOfZero predictions = predict(attributes);
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
