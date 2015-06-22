package quickml.supervised.classifier.downsampling;

import com.google.common.collect.Maps;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.Classifier;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Created by ian on 4/22/14.
 */
public class DownsamplingClassifier extends AbstractClassifier {

    private static final long serialVersionUID = -265699047882740160L;

    public final Classifier wrappedClassifier;
    private final Object minorityClassification;
    private final Object majorityClassification;
    private final double dropProbability;

    public DownsamplingClassifier(final Classifier wrappedClassifier, final Object majorityClassification, final Object minorityClassification, final double dropProbability) {
        this.wrappedClassifier = wrappedClassifier;
        this.majorityClassification = majorityClassification;
        this.minorityClassification = minorityClassification;
        this.dropProbability = dropProbability;
    }

    public double getProbability(AttributesMap attributes, Object classification) {
        double uncorrectedProbability = wrappedClassifier.getProbability(attributes, minorityClassification);
        double probabilityOfMinorityInstance = DownsamplingUtils.correctProbability(dropProbability, uncorrectedProbability);
        if (classification.equals(minorityClassification)) {
            return probabilityOfMinorityInstance;
        } else {
            return 1 - probabilityOfMinorityInstance;
        }
    }
    @Override
    public double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attributesToIgnore) {
        double uncorrectedProbability = wrappedClassifier.getProbabilityWithoutAttributes(attributes, minorityClassification, attributesToIgnore);
        double probabilityOfMinorityInstance = DownsamplingUtils.correctProbability(dropProbability, uncorrectedProbability);
        if (classification.equals(minorityClassification)) {
            return probabilityOfMinorityInstance;
        } else {
            return 1 - probabilityOfMinorityInstance;
        }
    }

    @Override
    public PredictionMap predict(AttributesMap attributes) {
        Map<Object, Double> probsByClassification = Maps.newHashMap();
        probsByClassification.put(minorityClassification, getProbability(attributes, minorityClassification));
        probsByClassification.put(majorityClassification, getProbability(attributes, majorityClassification));
        return new PredictionMap(probsByClassification);
    }

    @Override
    public PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        Map<Object, Double> probsByClassification = Maps.newHashMap();
        probsByClassification.put(minorityClassification, getProbabilityWithoutAttributes(attributes, minorityClassification, attributesToIgnore));
        probsByClassification.put(majorityClassification, getProbabilityWithoutAttributes(attributes, majorityClassification, attributesToIgnore));
        return new PredictionMap(probsByClassification);
    }

    @Override
    public Object getClassificationByMaxProb(final AttributesMap attributes) {
        return wrappedClassifier.getClassificationByMaxProb(attributes);
    }

    public double getDropProbability() {
        return dropProbability;
    }

    public Object getMajorityClassification() {
        return majorityClassification;
    }
}
