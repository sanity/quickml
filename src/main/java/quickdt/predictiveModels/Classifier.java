package quickdt.predictiveModels;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public abstract class Classifier extends AbstractPredictiveModel<Map<String, Serializable>, Map<Serializable, Double>> implements PredictiveModel<Map<String, Serializable>, Map<Serializable, Double>>  {

    public double getProbability(Map<String, Serializable> attributes, Serializable classification) {
        return predict(attributes).get(classification);
    }
    public abstract Map<Serializable, Double> predict(Map<String, Serializable> attributes);

    public Serializable getClassificationByMaxProb(Map<String, Serializable> attributes) {
        Map<Serializable, Double> predictions = predict(attributes);
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
