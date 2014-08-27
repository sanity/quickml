package quickml.supervised.classifier.downsampling;

import com.google.common.collect.Maps;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.Classifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 4/22/14.
 */
public class DownsamplingClassifier extends AbstractClassifier {
    private static final long serialVersionUID = -265699047882740160L;

    public final Classifier wrappedClassifier;
    private final Serializable minorityClassification;
    private final Serializable majorityClassification;
    private final double dropProbability;

    public DownsamplingClassifier(final Classifier wrappedClassifier, final Serializable majorityClassification, final Serializable minorityClassification, final double dropProbability) {
        this.wrappedClassifier = wrappedClassifier;
        this.majorityClassification = majorityClassification;
        this.minorityClassification = minorityClassification;
        this.dropProbability = dropProbability;
    }

    public double getProbability(AttributesMap attributes, Serializable classification) {
        double uncorrectedProbability = wrappedClassifier.getProbability(attributes, minorityClassification);
        double probabilityOfMinorityInstance = Utils.correctProbability(dropProbability, uncorrectedProbability);
        if (classification.equals(minorityClassification)) {
            return probabilityOfMinorityInstance;
        } else {
            return 1 - probabilityOfMinorityInstance;
        }

    }

    @Override
    public void dump(final Appendable appendable) {
        try {
            appendable.append("Will predict for downsampling with drop probability "+dropProbability+" for minority classification "+minorityClassification+"\n");
            wrappedClassifier.dump(appendable);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PredictionMap predict(AttributesMap attributes) {
        Map<Serializable, Double> probsByClassification = Maps.newHashMap();
        probsByClassification.put(minorityClassification, getProbability(attributes, minorityClassification));
        probsByClassification.put(majorityClassification, getProbability(attributes, majorityClassification));
        return new PredictionMap(probsByClassification);
    }

    @Override
    public Serializable getClassificationByMaxProb(final AttributesMap attributes) {
        return wrappedClassifier.getClassificationByMaxProb(attributes);
    }

    public double getDropProbability() {
        return dropProbability;
    }

    public Serializable getMajorityClassification() {
        return majorityClassification;
    }
}
