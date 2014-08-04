package quickdt.predictiveModels;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class ClassifierPrediction implements Prediction{
    Map<Serializable, Double> prediction;

    public Map<Serializable, Double> getPrediction(){
        return prediction;
    }

    public Double getPredictionForLabel(Serializable label) {
        return prediction.get(label);
    }

}
