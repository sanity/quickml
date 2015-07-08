package quickml.supervised.crossValidation;


import java.util.List;

/**
 * For a given validation set and predictive model, calculate the total loss
 * @param <PM>
 * @param <I>
 */

public interface LossChecker<PM, I> {
    public double calculateLoss(PM predictiveModel, List<I> validationSet);
}

