package quickml.supervised.classifier.temporallyWeightClassifier;

import quickml.data.MapWithDefaultOfZero;
import quickml.supervised.classifier.Classifier;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 6/20/14.
 */
public class TemporallyReweightedClassifier extends Classifier {
    private static final long serialVersionUID = 2642074639257374588L;
    private final Classifier wrappedClassifier;

    public TemporallyReweightedClassifier(Classifier classifier) {
        this.wrappedClassifier = classifier;
    }

    @Override
    public double getProbability(final Map<String, Serializable> attributes, final Serializable classification) {
        return wrappedClassifier.getProbability(attributes, classification);
    }

    @Override
    public MapWithDefaultOfZero predict(Map<String, Serializable> attributes) {
        return wrappedClassifier.predict(attributes);
    }

    @Override
    public void dump(final Appendable appendable) {
        wrappedClassifier.dump(appendable);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Map<String, Serializable> attributes) {
        return wrappedClassifier.getClassificationByMaxProb(attributes);
    }

    public Classifier getWrappedClassifier() {
        return wrappedClassifier;
    }

}
