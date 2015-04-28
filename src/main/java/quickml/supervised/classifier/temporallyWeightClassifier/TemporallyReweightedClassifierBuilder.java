package quickml.supervised.classifier.temporallyWeightClassifier;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Hours;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.tree.decisionTree.ClassificationCounter;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedClassifierBuilder implements PredictiveModelBuilder<TemporallyReweightedClassifier, InstanceWithAttributesMap> {

    public static final String HALF_LIFE_OF_NEGATIVE = "halfLifeOfNegative";
    public static final String HALF_LIFE_OF_POSITIVE = "halfLifeOfPositive";

    private static final double DEFAULT_DECAY_CONSTANT = 173; //approximately 5 days
    private double decayConstantOfPositive = DEFAULT_DECAY_CONSTANT;
    private double decayConstantOfNegative = DEFAULT_DECAY_CONSTANT;
    private final PredictiveModelBuilder<Classifier, InstanceWithAttributesMap> wrappedBuilder;
    private final Serializable positiveClassification;
    private final DateTimeExtractor dateTimeExtractor;

    public TemporallyReweightedClassifierBuilder(PredictiveModelBuilder<Classifier, InstanceWithAttributesMap> wrappedBuilder, Serializable positiveClassification, DateTimeExtractor dateTimeExtractor) {
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
    public TemporallyReweightedClassifier buildPredictiveModel(Iterable<InstanceWithAttributesMap> trainingData) {
        validateData(trainingData);
        DateTime mostRecent = getMostRecentInstance(trainingData);
        List<InstanceWithAttributesMap> trainingDataList = sortAndReweightTrainingData(trainingData, mostRecent);
        final Classifier predictiveModel = wrappedBuilder.buildPredictiveModel(trainingDataList);
        return new TemporallyReweightedClassifier(predictiveModel);
    }


    private List<InstanceWithAttributesMap> sortAndReweightTrainingData(Iterable<InstanceWithAttributesMap> trainingData, DateTime mostRecentInstance) {
        ArrayList<InstanceWithAttributesMap> sortedData = Lists.newArrayList();
        for (InstanceWithAttributesMap inst : trainingData) {
            sortedData.add(inst);
        }
        Collections.sort(sortedData, new Comparator<InstanceWithAttributesMap>() {
            @Override
            public int compare(InstanceWithAttributesMap o1, InstanceWithAttributesMap o2) {
                DateTime d1 = dateTimeExtractor.extractDateTime(o1);
                DateTime d2 = dateTimeExtractor.extractDateTime(o2);
                return -d1.compareTo(d2); //later times shoudl be sorted ahead of earlier times
            }
        });
        ArrayList<InstanceWithAttributesMap> trainingDataList = Lists.newArrayList();
        for (InstanceWithAttributesMap instance : sortedData) {
            double decayConstant = (instance.getLabel().equals(positiveClassification)) ? decayConstantOfPositive : decayConstantOfNegative;
            double hoursBack = Hours.hoursBetween(mostRecentInstance, dateTimeExtractor.extractDateTime(instance)).getHours();
            double newWeight = Math.exp(-1.0 * hoursBack / decayConstant);
            //TODO[mk] Reweight needs to be moved / removed
            trainingDataList.add(new InstanceWithAttributesMap(instance.getAttributes(), instance.getLabel(), newWeight));
        }
        return trainingDataList;
    }

    private void validateData(Iterable<InstanceWithAttributesMap> trainingData) {
        ClassificationCounter classificationCounter = ClassificationCounter.countAll(trainingData);
        Preconditions.checkArgument(classificationCounter.getCounts().keySet().size() <= 2, "trainingData must contain only 2 classifications, but it had %s", classificationCounter.getCounts().keySet().size());
    }

    private DateTime getMostRecentInstance(Iterable<InstanceWithAttributesMap> newData) {
        DateTime mostRecent = null;
        for (InstanceWithAttributesMap instance : newData) {
            if (mostRecent == null || dateTimeExtractor.extractDateTime(instance).isAfter(mostRecent)) {
                mostRecent = dateTimeExtractor.extractDateTime(instance);
            }
        }
        return mostRecent;
    }

}
