package quickml.supervised.alternative.optimizer;

import java.util.List;

public interface TrainingDataCycler<T> {

    List<T> getTrainingSet();

    List<T> getValidationSet();

    void nextCycle();

    void reset();

    List<T> getAllData();

    boolean hasMore();

}
