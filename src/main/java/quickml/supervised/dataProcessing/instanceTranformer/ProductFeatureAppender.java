package quickml.supervised.dataProcessing.instanceTranformer;

import quickml.data.instances.InstanceWithAttributesMap;

import java.util.List;

/**
 * Created by alexanderhawk on 10/22/15.
 */
public interface ProductFeatureAppender<I extends InstanceWithAttributesMap> {
    public List<I> addProductAttributes(List<I> trainingData);
}
