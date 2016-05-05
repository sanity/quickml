package quickml.supervised.crossValidation;

import quickml.data.instances.Instance;
import quickml.supervised.PredictiveModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public interface CrossValidator {
    double getLossForModel();
    double getLossForModel(Map<String, Serializable> config);
}
