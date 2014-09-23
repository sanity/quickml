package quickml.supervised.classifier;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface Classifier extends PredictiveModel<AttributesMap, PredictionMap> {

    public double getProbability(AttributesMap attributes, Serializable classification);
    public abstract PredictionMap predict(AttributesMap attributes);
    public Serializable getClassificationByMaxProb(AttributesMap attributes);
}
