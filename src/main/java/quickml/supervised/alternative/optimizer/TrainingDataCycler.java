package quickml.supervised.alternative.optimizer;

import quickml.data.Instance;

import java.util.List;

public interface TrainingDataCycler<T> {

    void reset();

    List<T> getTrainingSet();

    List<T> getValidationSet();

    List<T> getAllData();

    void nextCycle();

    boolean hasMore();

}
