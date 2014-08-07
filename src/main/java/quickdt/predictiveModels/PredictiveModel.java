package quickdt.predictiveModels;

import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.AbstractInstance;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PredictiveModel<Pr> extends Serializable {
    <R> Pr predict(R regressors);
    void dump(PrintStream printStream);
    List<LabelPredictionWeight<Pr>> createLabelPredictionWeights(List<AbstractInstance> instances);
}
