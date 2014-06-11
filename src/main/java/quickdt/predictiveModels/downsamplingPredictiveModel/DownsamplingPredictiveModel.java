package quickdt.predictiveModels.downsamplingPredictiveModel;

import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 4/22/14.
 */
public class DownsamplingPredictiveModel implements PredictiveModel {
    private static final long serialVersionUID = -265699047882740160L;

    public final PredictiveModel wrappedPredictiveModel;
    private final Serializable minorityClassification;
    private final Serializable majorityClassification;
    private final double dropProbability;

    public DownsamplingPredictiveModel(final PredictiveModel wrappedPredictiveModel, final Serializable majorityClassification, final Serializable minorityClassification, final double dropProbability) {
        this.wrappedPredictiveModel = wrappedPredictiveModel;
        this.majorityClassification = majorityClassification;
        this.minorityClassification = minorityClassification;
        this.dropProbability = dropProbability;
    }

    @Override
    public double getProbability(final Attributes attributes, final Serializable classification) {
        double uncorrectedProbability = wrappedPredictiveModel.getProbability(attributes, minorityClassification);
        double probabilityOfMinorityInstance = Utils.correctProbability(dropProbability, uncorrectedProbability);
        if (classification.equals(minorityClassification)) {
            return probabilityOfMinorityInstance;
        } else {
            return 1 - probabilityOfMinorityInstance;
        }

    }

    /**
     * Unsupported at this time, will throw UnsupportedOperationException
     * @param attributes
     * @return
     */
    @Override
    public Map<Serializable, Double> getProbabilitiesByClassification(final Attributes attributes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void dump(final PrintStream printStream) {
        printStream.println("Will correct for downsampling with drop probability "+dropProbability+" for minority classification "+minorityClassification);
        wrappedPredictiveModel.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(final Attributes attributes) {
        return wrappedPredictiveModel.getClassificationByMaxProb(attributes);
    }

    public double getDropProbability() {
        return dropProbability;
    }

    public Serializable getMajorityClassification() {
        return majorityClassification;
    }
}
