package quickdt.crossValidation;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

/**
 * Created by Chris on 5/5/2014.
 */
public interface CrossValLoss<S extends CrossValLoss> extends Comparable<S> {
    public void addLoss(AbstractInstance abstractInstance, PredictiveModel predictiveModel);
    public double getTotalLoss();
}
