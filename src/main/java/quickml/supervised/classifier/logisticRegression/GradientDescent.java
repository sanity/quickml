package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/12/15.
 */
public interface GradientDescent<I extends Instance> {
     double[] minimize(List<I> instances, int numFeatures);
     void updateBuilderConfig(final Map<String, Serializable> config);


}
