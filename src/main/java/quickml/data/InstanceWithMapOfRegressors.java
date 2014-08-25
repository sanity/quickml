package quickml.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/22/14.
 */
public class InstanceWithMapOfRegressors extends InstanceImpl<Map<String, Serializable>> {
    public InstanceWithMapOfRegressors(final Map<String, Serializable> regressors, final Serializable label) {
        super(regressors, label, DEFAULT_WEIGHT);
    }

    public InstanceWithMapOfRegressors(final Map<String, Serializable> regressors, final Serializable label, final double weight) {
       super(regressors, label, weight);
    }

}
