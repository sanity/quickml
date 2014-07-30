package quickdt.predictiveModels;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 7/30/14.
 */
public class Prediction<P extends Serializable> {
    P prediction;

    public Prediction(P prediction) {
        this.prediction = prediction;
    }
}
