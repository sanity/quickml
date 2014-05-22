package quickdt.predictiveModels.wrappedPredictiveModel;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Created by chrisreeves on 5/22/14.
 */
public class WrappedPredictiveModel implements PredictiveModel {
    public PredictiveModel predictiveModel;

    public WrappedPredictiveModel (PredictiveModel predictiveModel) {
        this.predictiveModel = predictiveModel;
    }

    public double getProbability(Attributes attributes, Serializable classification) {
        return predictiveModel.getProbability(attributes, classification);
    }


    public void dump(PrintStream printStream) {
        predictiveModel.dump(printStream);
    }

    public Serializable getClassificationByMaxProb(Attributes attributes) {
        return predictiveModel.getClassificationByMaxProb(attributes);
    }
}
