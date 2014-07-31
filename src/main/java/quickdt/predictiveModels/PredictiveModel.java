package quickdt.predictiveModels;

import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PredictiveModel<P extends Prediction> extends Serializable {
    P predict(Attributes instance);
    void dump(PrintStream printStream);

}
