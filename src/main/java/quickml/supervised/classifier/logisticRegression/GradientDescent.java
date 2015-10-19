package quickml.supervised.classifier.logisticRegression;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public interface GradientDescent {
     double[] minimize(List<SparseClassifierInstance> sparseClassifierInstances, int numFeatures);
     void updateBuilderConfig(final Map<String, Serializable> config);


}
