package quickdt.experiments.crossValidation;

/**
 * Created by ian on 2/28/14.
 */
public abstract class CrossValScorer<S extends CrossValScorer> implements Comparable<S> {
    public abstract void score(double probabilityOfCorrectInstance);
}
