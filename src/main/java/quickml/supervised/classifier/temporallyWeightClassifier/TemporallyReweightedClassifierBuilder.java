package quickml.supervised.classifier.temporallyWeightClassifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Hours;
import quickml.data.AttributesMap;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.data.Instance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.decisionTree.tree.ClassificationCounter;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedClassifierBuilder implements PredictiveModelBuilder<AttributesMap, Serializable, TemporallyReweightedClassifier> {
    private static final double DEFAULT_DECAY_CONSTANT = 173; //approximately 5 days
    private double decayConstantOfPositive = DEFAULT_DECAY_CONSTANT;
    private double decayConstantOfNegative = DEFAULT_DECAY_CONSTANT;
    private final PredictiveModelBuilder<AttributesMap, Serializable, ? extends Classifier> wrappedBuilder;
    private final DateTimeExtractor dateTimeExtractor;
    private final Serializable positiveClassification;

    public TemporallyReweightedClassifierBuilder(PredictiveModelBuilder<AttributesMap, Serializable, ? extends Classifier> wrappedBuilder, DateTimeExtractor dateTimeExtractor) {
        this(wrappedBuilder, dateTimeExtractor, 1.0);
    }

    public TemporallyReweightedClassifierBuilder(PredictiveModelBuilder<AttributesMap, Serializable, ? extends Classifier> wrappedBuilder, DateTimeExtractor dateTimeExtractor, Serializable positiveClassification) {
        this.wrappedBuilder = wrappedBuilder;
        this.dateTimeExtractor = dateTimeExtractor;
        this.positiveClassification = positiveClassification;
    }

    public TemporallyReweightedClassifierBuilder halfLifeOfPositive(double halfLifeOfPositiveInDays) {
        this.decayConstantOfPositive = halfLifeOfPositiveInDays * DateTimeConstants.HOURS_PER_DAY / Math.log(2);
        return this;
    }

    public TemporallyReweightedClassifierBuilder halfLifeOfNegative(double halfLifeOfNegativeInDays) {
        this.decayConstantOfNegative = halfLifeOfNegativeInDays * DateTimeConstants.HOURS_PER_DAY / Math.log(2);
        return this;
    }


    @Override
    public TemporallyReweightedClassifier buildPredictiveModel(Iterable<? extends Instance<AttributesMap, Serializable>> trainingData) {
        validateData(trainingData);
        DateTime mostRecent = getMostRecentInstance(trainingData);
        List<Instance<AttributesMap, Serializable>> trainingDataList = reweightTrainingData(trainingData, mostRecent);
        final Classifier predictiveModel = wrappedBuilder.buildPredictiveModel(trainingDataList);
        return new TemporallyReweightedClassifier(predictiveModel);
    }


    private List<Instance<AttributesMap, Serializable>> reweightTrainingData(Iterable<? extends Instance<AttributesMap, Serializable>> sortedData, DateTime mostRecentInstance) {
        ArrayList<Instance<AttributesMap, Serializable>> trainingDataList = Lists.newArrayList();
        for (Instance<AttributesMap, Serializable> instance : sortedData) {
            double decayConstant = (instance.getLabel().equals(positiveClassification)) ? decayConstantOfPositive : decayConstantOfNegative;
            DateTime timeOfInstance = dateTimeExtractor.extractDateTime(instance);
            double hoursBack = Hours.hoursBetween(mostRecentInstance, timeOfInstance).getHours();
            double newWeight = Math.exp(-1.0 * hoursBack / decayConstant);
            //TODO[mk] Reweight needs to be moved / removed
//            trainingDataList.add(instance.reweight(newWeight));
        }
        return trainingDataList;
    }

    private void validateData(Iterable<? extends Instance<AttributesMap, Serializable>> trainingData) {
        ClassificationCounter classificationCounter = ClassificationCounter.countAll(trainingData);
        Preconditions.checkArgument(classificationCounter.getCounts().keySet().size() <= 2, "trainingData must contain only 2 classifications, but it had %s", classificationCounter.getCounts().keySet().size());
    }

    private DateTime getMostRecentInstance(Iterable<? extends Instance<AttributesMap, Serializable>> newData) {
        DateTime mostRecent = null;
        for (Instance<AttributesMap, Serializable> instance : newData) {
            DateTime instanceTime = dateTimeExtractor.extractDateTime(instance);
            if (mostRecent == null || instanceTime.isAfter(mostRecent)) {
                mostRecent = instanceTime;
            }
        }
        return mostRecent;
    }

}
