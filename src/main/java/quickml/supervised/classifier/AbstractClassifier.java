package quickml.supervised.classifier;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by alexanderhawk on 8/17/14.
 */
//where do we want Classifier as a generic type...in downsampling PMB.
public abstract class AbstractClassifier implements Classifier {


    private static final long serialVersionUID = -5052476771686106526L;
    public double getProbability(AttributesMap attributes, Serializable classification) {
        return predict(attributes).get(classification);
    }

    public double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore) {
        return predictWithoutAttributes(attributes, attributesToIgnore).get(classification);
    }

    public Serializable getClassificationByMaxProb(AttributesMap attributes) {
        PredictionMap predictions = predict(attributes);
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
