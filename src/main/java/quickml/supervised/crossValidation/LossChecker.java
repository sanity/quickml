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
public interface LossChecker<PM extends PredictiveModel<AttributesMap, ?>, I extends InstanceWithAttributesMap<?>> {
    public double calculateLoss(PM predictiveModel, List<I> validationSet);
}
