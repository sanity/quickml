package quickdt.predictiveModels;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class RealValuedFunctionPrediction implements Prediction{
    public double prediction;

    public double getPrediction(){
        return prediction;
    }

}
