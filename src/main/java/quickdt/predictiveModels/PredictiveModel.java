package quickdt.predictiveModels;

import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.AbstractInstance;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
<<<<<<< HEAD
public interface PredictiveModel<R, P> {
=======
public interface PredictiveModel<R, P> extends Serializable {
>>>>>>> origin/regRefactorTake2-ian
    P predict(R regressors);
    void dump(Appendable appendable);
    List<LabelPredictionWeight<P>> createLabelPredictionWeights(List<AbstractInstance<R>> instances);
}
