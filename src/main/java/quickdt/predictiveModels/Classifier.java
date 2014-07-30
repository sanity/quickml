package quickdt.predictiveModels;

import quickdt.data.Attributes;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface Classifier extends RealValuedFunction {
    double getProbability(Attributes attributes, Serializable classification);
    Map<Serializable, Double> getProbabilitiesByClassification(Attributes attributes);
    Serializable getClassificationByMaxProb(Attributes attributes);
}
