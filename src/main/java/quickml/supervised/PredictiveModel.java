package quickml.supervised;

import java.io.Serializable;
import java.util.Set;

/**
 * A predictive model, typically created by a supervised learning algorithm.
 * Given a set of attributes, will generate a prediction.
 */
public interface PredictiveModel<A, P> extends Serializable {

    P predict(A attributes);
    P predictWithoutAttributes(A attributes, Set<String> attributesToIgnore);
    void dump(Appendable appendable);
}
