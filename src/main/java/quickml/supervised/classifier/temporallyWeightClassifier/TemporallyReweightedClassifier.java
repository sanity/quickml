package quickml.supervised.classifier.temporallyWeightClassifier;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.Classifier;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by alexanderhawk on 6/20/14.
 */
public class TemporallyReweightedClassifier extends AbstractClassifier {

    private static final long serialVersionUID = 2642074639257374588L;
    private final Classifier wrappedClassifier;

    public TemporallyReweightedClassifier(Classifier classifier) {
        this.wrappedClassifier = classifier;
    }

    @Override
    public double getProbability(final AttributesMap attributes, final Serializable classification) {
        return wrappedClassifier.getProbability(attributes, classification);
    }

    @Override
    public PredictionMap predict(AttributesMap attributes) {
        return wrappedClassifier.predict(attributes);
    }

    @Override
    public double getProbabilityWithoutAttributes(final AttributesMap attributes, final Serializable classification, Set<String> attributesToIgnore) {
        return wrappedClassifier.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
    }

    @Override
    public PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        return wrappedClassifier.predictWithoutAttributes(attributes, attributesToIgnore);
    }

    @Override
    public void dump(final Appendable appendable) {
        wrappedClassifier.dump(appendable);
    }

    @Override
    public Serializable getClassificationByMaxProb(final AttributesMap attributes) {
        return wrappedClassifier.getClassificationByMaxProb(attributes);
    }

    public Classifier getWrappedClassifier() {
        return wrappedClassifier;
    }

}
