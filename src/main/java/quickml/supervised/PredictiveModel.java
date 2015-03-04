package quickml.supervised;

import java.io.Serializable;
import java.util.Set;

/**
 * A predictive model, typically created by a supervised learning algorithm.
 * Given a set of input values, will generate a prediction.
 */
public interface PredictiveModel<INPUT, PREDICTION> extends Serializable {

    PREDICTION predict(INPUT input);

    // TODO: Deprecated because this method is too specialized to belong in this interface.
    // TODO: It needs to be removed from here.
    @Deprecated()
    PREDICTION predictWithoutAttributes(INPUT input, Set<String> attributesToIgnore);
    void dump(Appendable appendable);
}
