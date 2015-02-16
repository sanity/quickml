package quickml.supervised.crossValidation;

import java.util.List;

public interface LossChecker<PM, T> {

    public double calculateLoss(PM predictiveModel, List<T> validationSet);
}
