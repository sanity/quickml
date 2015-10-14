package quickml.supervised.tree.reducers;

import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public interface ReducerFactory<I extends InstanceWithAttributesMap<?>, VC extends ValueCounter<VC>> {

    Reducer<I, VC> getReducer(List<I> trainingData);

    void updateBuilderConfig(Map<String, Serializable> cfg);


}
