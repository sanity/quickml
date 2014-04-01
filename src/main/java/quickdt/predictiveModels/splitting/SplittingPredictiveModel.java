package quickdt.predictiveModels.splitting;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Created by ian on 4/1/14.
 */
public class SplittingPredictiveModel implements PredictiveModel {
    private static final long serialVersionUID = -595493843003967661L;

    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        return 0;
    }

    @Override
    public void dump(final PrintStream printStream) {

    }

    @Override
    public Serializable getClassificationByMaxProb(final Attributes attributes) {
        return null;
    }
}
