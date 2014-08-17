package quickml.supervised.classifier;

import quickml.data.MapWithDefaultOfZero;
import quickml.supervised.AbstractPredictiveModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/17/14.
 */
//where do we want Classifier as a generic type...in downsampling PMB.
public abstract class AbstractClassifier extends AbstractPredictiveModel<Map<String, Serializable>, MapWithDefaultOfZero> implements Classifier {
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
