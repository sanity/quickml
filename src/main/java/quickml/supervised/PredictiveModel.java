package quickml.supervised;

import java.io.Serializable;
import java.util.Set;

/**
 * A predictive model, typically created by a supervised learning algorithm.
 * Given a set of input values, will generate a prediction.
 */
public interface PredictiveModel<A, P> extends Serializable {

    P predict(A attributes);

    // TODO: Deprecated because this method is too specialized to belong in this interface.
    // TODO: It needs to be removed from here.
    @Deprecated()
    P predictWithoutAttributes(A attributes, Set<String> attributesToIgnore);
    void dump(Appendable appendable);
}
