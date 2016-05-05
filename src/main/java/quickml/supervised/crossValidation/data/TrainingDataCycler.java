package quickml.supervised.crossValidation.data;

import java.util.List;

/**
 * A training data cycler should take a set of training instances and cycle through different treeBuildContexts of training and
 * validation sets.
 * @param <I>
 */
public interface TrainingDataCycler<I> {

    List<I> getTrainingSet();

    List<I> getValidationSet();

    boolean nextCycle();

    void reset();

    List<I> getAllData();

    boolean hasMore();

}
