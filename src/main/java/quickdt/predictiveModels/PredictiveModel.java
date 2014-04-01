package quickdt.predictiveModels;

import quickdt.data.Attributes;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PredictiveModel extends Serializable {
    double getProbability(Attributes attributes, Serializable classification);  //refactor to getUncalibratedProbability()

    public void dump(PrintStream printStream);

    public Serializable getClassificationByMaxProb(Attributes attributes);
}
