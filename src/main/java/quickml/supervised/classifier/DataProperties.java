package quickml.supervised.classifier;

import quickml.data.InstanceWithAttributesMap;

import java.util.List;

/**
 * Created by alexanderhawk on 3/24/15.
 */
public abstract class DataProperties<L, T extends InstanceWithAttributesMap<L>> {
    public DataProperties(List<T> trainingData) {
        this.trainingData = trainingData;
    }

    protected List<T> trainingData;
}
