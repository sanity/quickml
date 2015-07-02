package quickml.supervised.classifier;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModel;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface Classifier extends PredictiveModel<AttributesMap, PredictionMap> {

    double getProbability(AttributesMap attributes, Serializable classification);
    double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore);
    PredictionMap predict(AttributesMap attributes);
    PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore);
    Serializable getClassificationByMaxProb(AttributesMap attributes);
}
