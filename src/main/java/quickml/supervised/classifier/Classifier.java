package quickml.supervised.classifier;

import quickml.data.MapWithDefaultOfZero;
import quickml.supervised.PredictiveModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface Classifier extends PredictiveModel<Map<String, Serializable>, MapWithDefaultOfZero> {

    public double getProbability(Map<String, Serializable> attributes, Serializable classification);
    public abstract MapWithDefaultOfZero predict(Map<String, Serializable> attributes);
    public Serializable getClassificationByMaxProb(Map<String, Serializable> attributes);
}
