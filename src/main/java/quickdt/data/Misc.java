package quickdt.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/11/14.
 */
public class Misc {
    private static final double DEFAULT_WEIGHT = 1.0;

    public Instance createInstanceWithMapOfRegressors(final Map<String, Serializable> regressors, final Serializable label) {
        return createInstanceWithMapOfRegressors(regressors, label, DEFAULT_WEIGHT);
    }

    public Instance createInstanceWithMapOfRegressors(final Map<String, Serializable> regressors, final Serializable label, final double weight) {
        return new InstanceImpl(regressors, label, weight);
    }
}
