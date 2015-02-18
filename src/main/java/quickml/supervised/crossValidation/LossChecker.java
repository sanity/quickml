package quickml.supervised.crossValidation;

import java.util.List;

/**
 * For a given validation set and predictive model, calculate the total loss
 * @param <PM>
 * @param <T>
 */
public interface LossChecker<PM, T> {
    public double calculateLoss(PM predictiveModel, List<T> validationSet);
}
