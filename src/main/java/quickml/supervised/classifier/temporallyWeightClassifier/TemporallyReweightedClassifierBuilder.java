package quickml.supervised.classifier.temporallyWeightClassifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Hours;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.decisionTree.tree.ClassificationCounter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedClassifierBuilder implements PredictiveModelBuilder<TemporallyReweightedClassifier, ClassifierInstance> {

    public static final String HALF_LIFE_OF_NEGATIVE = "halfLifeOfNegative";
    public static final String HALF_LIFE_OF_POSITIVE = "halfLifeOfPositive";

    private static final double DEFAULT_DECAY_CONSTANT = 173; //approximately 5 days
    private double decayConstantOfPositive = DEFAULT_DECAY_CONSTANT;
    private double decayConstantOfNegative = DEFAULT_DECAY_CONSTANT;
    private final PredictiveModelBuilder<Classifier, ClassifierInstance> wrappedBuilder;
    private final Serializable positiveClassification;

    public TemporallyReweightedClassifierBuilder(PredictiveModelBuilder<Classifier, ClassifierInstance> wrappedBuilder, Serializable positiveClassification) {
        this.wrappedBuilder = wrappedBuilder;
        this.positiveClassification = positiveClassification;
    }

    @Override
    public void updateBuilderConfig(Map<String, Object> config) {
        wrappedBuilder.updateBuilderConfig(config);
        halfLifeOfPositive((Double) config.get(HALF_LIFE_OF_POSITIVE));
        halfLifeOfNegative((Double) config.get(HALF_LIFE_OF_NEGATIVE));
    }

    public TemporallyReweightedClassifierBuilder halfLifeOfPositive(double halfLifeOfPositiveInDays) {
        this.decayConstantOfPositive = halfLifeOfPositiveInDays * DateTimeConstants.HOURS_PER_DAY / Math.log(2);
        return this;
    }

    public TemporallyReweightedClassifierBuilder halfLifeOfNegative(double halfLifeOfNegativeInDays) {
        this.decayConstantOfNegative = halfLifeOfNegativeInDays * DateTimeConstants.HOURS_PER_DAY / Math.log(2);
        return this;
    }

    public TemporallyReweightedClassifierBuilder DateTimeExtractor(double halfLifeOfNegativeInDays) {
        this.decayConstantOfNegative = halfLifeOfNegativeInDays * DateTimeConstants.HOURS_PER_DAY / Math.log(2);
        return this;
    }

    @Override
    public TemporallyReweightedClassifier buildPredictiveModel(Iterable<ClassifierInstance> trainingData) {
        validateData(trainingData);
        DateTime mostRecent = getMostRecentInstance(trainingData);
        List<ClassifierInstance> trainingDataList = reweightTrainingData(trainingData, mostRecent);
        final Classifier predictiveModel = wrappedBuilder.buildPredictiveModel(trainingDataList);
        return new TemporallyReweightedClassifier(predictiveModel);
    }


    private List<ClassifierInstance> reweightTrainingData(Iterable<ClassifierInstance> sortedData, DateTime mostRecentInstance) {
        ArrayList<ClassifierInstance> trainingDataList = Lists.newArrayList();
        for (ClassifierInstance instance : sortedData) {
            double decayConstant = (instance.getLabel().equals(positiveClassification)) ? decayConstantOfPositive : decayConstantOfNegative;
            double hoursBack = Hours.hoursBetween(mostRecentInstance, instance.getTimestamp()).getHours();
            double newWeight = Math.exp(-1.0 * hoursBack / decayConstant);
            //TODO[mk] Reweight needs to be moved / removed
//            trainingDataList.add(instance.reweight(newWeight));
        }
        return trainingDataList;
    }

    private void validateData(Iterable<ClassifierInstance> trainingData) {
        ClassificationCounter classificationCounter = ClassificationCounter.countAll(trainingData);
        Preconditions.checkArgument(classificationCounter.getCounts().keySet().size() <= 2, "trainingData must contain only 2 classifications, but it had %s", classificationCounter.getCounts().keySet().size());
    }

    private DateTime getMostRecentInstance(Iterable<ClassifierInstance> newData) {
        DateTime mostRecent = null;
        for (ClassifierInstance instance : newData) {
            if (mostRecent == null || instance.getTimestamp().isAfter(mostRecent)) {
                mostRecent = instance.getTimestamp();
            }
        }
        return mostRecent;
    }

}