package quickml.supervised.crossValidation.data;

import java.util.List;

/**
 * A training data cycler should take a set of training instances and cycle through different configurations of training and
 * validation sets.
 * @param <T>
 */
public interface TrainingDataCycler<T> {

    List<T> getTrainingSet();

    List<T> getValidationSet();

    void nextCycle();

    void reset();

    List<T> getAllData();

    boolean hasMore();

}
