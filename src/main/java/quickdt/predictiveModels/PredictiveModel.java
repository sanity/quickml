package quickdt.predictiveModels;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import quickdt.crossValidation.CrossValidator;
import quickdt.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PredictiveModel<Pr extends Prediction> extends Serializable {
    Pr predict(Attributes instance);
    void dump(PrintStream printStream);
    List<LabelPredictionWeight<Pr>> createLabelPredictionWeights(List<AbstractInstance> instances);
}
