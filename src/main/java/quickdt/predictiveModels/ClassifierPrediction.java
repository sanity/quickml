package quickdt.predictiveModels;

import quickdt.data.Attributes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class ClassifierPrediction implements Prediction{
    HashMap<Serializable, Double> prediction;

    public HashMap<Serializable, Double> getPrediction(){
        return prediction;
    }

}
