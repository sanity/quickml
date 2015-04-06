package quickml.supervised.classifier;

import java.util.List;

/**
 * Created by alexanderhawk on 3/24/15.
 */
public abstract class DataProperties<T> {
    public DataProperties(List<T> trainingData) {
        this.trainingData = trainingData;
    }

    protected List<T> trainingData;
}
