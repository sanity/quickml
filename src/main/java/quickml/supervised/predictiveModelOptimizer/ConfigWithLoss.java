package quickml.supervised.predictiveModelOptimizer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 9/24/15.
 */
public class ConfigWithLoss {

    double loss;
    Map<String, Serializable> config;

    public ConfigWithLoss(final double loss, final Map<String, Serializable> config) {
        this.loss = loss;
        this.config = config;
    }

}