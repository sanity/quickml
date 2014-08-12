package quickdt.predictiveModels.downsamplingPredictiveModel;

import com.google.common.collect.Maps;
import quickdt.data.MapWithDefaultOfZero;
import quickdt.predictiveModels.Classifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 4/22/14.
 */
public class DownsamplingClassifier extends Classifier {
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

    public double getProbability(Map<String, Serializable> attributes, Serializable classification) {
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
    public MapWithDefaultOfZero predict(Map<String, Serializable> attributes) {
        Map<Serializable, Double> probsByClassification = Maps.newHashMap();
        probsByClassification.put(minorityClassification, getProbability(attributes, minorityClassification));
        probsByClassification.put(majorityClassification, getProbability(attributes, majorityClassification));
        return new MapWithDefaultOfZero(probsByClassification);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Map<String, Serializable> attributes) {
        return wrappedClassifier.getClassificationByMaxProb(attributes);
    }

    public double getDropProbability() {
        return dropProbability;
    }

    public Serializable getMajorityClassification() {
        return majorityClassification;
    }
}
