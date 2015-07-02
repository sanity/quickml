package quickml.supervised.crossValidation;

import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModel;

import java.util.List;

/**
 * For a given validation set and predictive model, calculate the total loss
 * @param <PM>
 * @param <I>
 */
public interface LossChecker<A, PM extends PredictiveModel<A, ?>, I extends Instance<A, ?>> {
    public double calculateLoss(PM predictiveModel, List<I> validationSet);
}
