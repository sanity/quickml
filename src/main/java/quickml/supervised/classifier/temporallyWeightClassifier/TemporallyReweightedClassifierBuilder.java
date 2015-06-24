package quickml.supervised.classifier.temporallyWeightClassifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Hours;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;
import quickml.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedClassifierBuilder implements PredictiveModelBuilder<AttributesMap, TemporallyReweightedClassifier, ClassifierInstance> {

    public static final String HALF_LIFE_OF_NEGATIVE = "halfLifeOfNegative";
    public static final String HALF_LIFE_OF_POSITIVE = "halfLifeOfPositive";

    private static final double DEFAULT_DECAY_CONSTANT = 173; //approximately 5 days
    private double decayConstantOfPositive = DEFAULT_DECAY_CONSTANT;
    private double decayConstantOfNegative = DEFAULT_DECAY_CONSTANT;
    private final PredictiveModelBuilder<AttributesMap, Classifier, ClassifierInstance> wrappedBuilder;
    private final Serializable positiveClassification;
    private final DateTimeExtractor dateTimeExtractor;

    public TemporallyReweightedClassifierBuilder(PredictiveModelBuilder<AttributesMap, Classifier, ClassifierInstance> wrappedBuilder, Serializable positiveClassification, DateTimeExtractor dateTimeExtractor) {
        this.wrappedBuilder = wrappedBuilder;
        this.positiveClassification = positiveClassification;
        this.dateTimeExtractor = dateTimeExtractor;
    }

    @Override
    public void updateBuilderConfig(Map<String, Object> config) {
        wrappedBuilder.updateBuilderConfig(config);
        if (config.containsKey(HALF_LIFE_OF_NEGATIVE))
            halfLifeOfNegative((Double) config.get(HALF_LIFE_OF_NEGATIVE));
        if (config.containsKey(HALF_LIFE_OF_POSITIVE))
            halfLifeOfPositive((Double) config.get(HALF_LIFE_OF_POSITIVE));
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
        List<ClassifierInstance> trainingDataList = sortAndReweightTrainingData(trainingData, mostRecent);
        final Classifier predictiveModel = wrappedBuilder.buildPredictiveModel(trainingDataList);
        return new TemporallyReweightedClassifier(predictiveModel);
    }


    private List<ClassifierInstance> sortAndReweightTrainingData(Iterable<ClassifierInstance> trainingData, DateTime mostRecentInstance) {
        ArrayList<ClassifierInstance> sortedData = Lists.newArrayList();
        for (ClassifierInstance inst : trainingData) {
            sortedData.add(inst);
        }
        Collections.sort(sortedData, new Comparator<ClassifierInstance>() {
            @Override
            public int compare(ClassifierInstance o1, ClassifierInstance o2) {
                DateTime d1 = dateTimeExtractor.extractDateTime(o1);
                DateTime d2 = dateTimeExtractor.extractDateTime(o2);
                return -d1.compareTo(d2); //later times shoudl be sorted ahead of earlier times
            }
        });
        ArrayList<ClassifierInstance> trainingDataList = Lists.newArrayList();
        for (ClassifierInstance instance : sortedData) {
            double decayConstant = (instance.getLabel().equals(positiveClassification)) ? decayConstantOfPositive : decayConstantOfNegative;
            double hoursBack = Hours.hoursBetween(mostRecentInstance, dateTimeExtractor.extractDateTime(instance)).getHours();
            double newWeight = Math.exp(-1.0 * hoursBack / decayConstant);
            //TODO[mk] Reweight needs to be moved / removed
            trainingDataList.add(new ClassifierInstance(instance.getAttributes(), instance.getLabel(), newWeight));
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
            if (mostRecent == null || dateTimeExtractor.extractDateTime(instance).isAfter(mostRecent)) {
                mostRecent = dateTimeExtractor.extractDateTime(instance);
            }
        }
        return mostRecent;
    }

}
