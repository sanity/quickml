package quickml.supervised.crossValidation.data;

import java.util.List;

/**
 *
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
