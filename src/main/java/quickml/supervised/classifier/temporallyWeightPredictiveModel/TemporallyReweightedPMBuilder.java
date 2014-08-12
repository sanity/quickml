package quickml.supervised.classifier.temporallyWeightPredictiveModel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Hours;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.data.Instance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.UpdatablePredictiveModelBuilder;
import quickml.supervised.classifier.decisionTree.tree.ClassificationCounter;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedPMBuilder implements UpdatablePredictiveModelBuilder<Map<String, Serializable>,TemporallyReweightedPM> {
    private static final double DEFAULT_DECAY_CONSTANT = 173; //approximately 5 days
    private double decayConstantOfPositive = DEFAULT_DECAY_CONSTANT;
    private double decayConstantOfNegative = DEFAULT_DECAY_CONSTANT;
    private final PredictiveModelBuilder<Map<String, Serializable>, Classifier> wrappedBuilder;
    private final DateTimeExtractor dateTimeExtractor;
    private final Serializable positiveClassification;

    public TemporallyReweightedPMBuilder(PredictiveModelBuilder<Map<String, Serializable>, Classifier> wrappedBuilder, DateTimeExtractor dateTimeExtractor) {
        this(wrappedBuilder, dateTimeExtractor, 1.0);
    }

    public TemporallyReweightedPMBuilder(PredictiveModelBuilder<Map<String, Serializable>, Classifier> wrappedBuilder, DateTimeExtractor dateTimeExtractor, Serializable positiveClassification) {
        this.wrappedBuilder = wrappedBuilder;
        this.dateTimeExtractor = dateTimeExtractor;
        this.positiveClassification = positiveClassification;
    }

    public TemporallyReweightedPMBuilder halfLifeOfPositive(double halfLifeOfPositiveInDays) {
        this.decayConstantOfPositive = halfLifeOfPositiveInDays * DateTimeConstants.HOURS_PER_DAY / Math.log(2);
        return this;
    }

    public TemporallyReweightedPMBuilder halfLifeOfNegative(double halfLifeOfNegativeInDays) {
        this.decayConstantOfNegative = halfLifeOfNegativeInDays * DateTimeConstants.HOURS_PER_DAY / Math.log(2);
        return this;
    }

    @Override
    public void setID(Serializable iD) {
        wrappedBuilder.setID(iD);
    }

    @Override
    public TemporallyReweightedPM buildPredictiveModel(Iterable<Instance<Map<String, Serializable>>> trainingData) {
        validateData(trainingData);
        DateTime mostRecent = getMostRecentInstance(trainingData);
        List<Instance<Map<String, Serializable>>> trainingDataList = reweightTrainingData(trainingData, mostRecent);
        final Classifier predictiveModel = wrappedBuilder.buildPredictiveModel(trainingDataList);
        return new TemporallyReweightedPM(predictiveModel);
    }

    @Override
    public PredictiveModelBuilder<Map<String, Serializable>, TemporallyReweightedPM> updatable(boolean updatable) {
        wrappedBuilder.updatable(updatable);
        return this;
    }

    private List<Instance<Map<String, Serializable>>> reweightTrainingData(Iterable<? extends Instance> sortedData, DateTime mostRecentInstance) {
        ArrayList<Instance<Map<String, Serializable>>> trainingDataList = Lists.newArrayList();
        for (Instance<Map<String, Serializable>> instance : sortedData) {
            double decayConstant = (instance.getLabel().equals(positiveClassification)) ? decayConstantOfPositive : decayConstantOfNegative;
            DateTime timeOfInstance = dateTimeExtractor.extractDateTime(instance);
            double hoursBack = Hours.hoursBetween(mostRecentInstance, timeOfInstance).getHours();
            double newWeight = Math.exp(-1.0 * hoursBack / decayConstant);
            trainingDataList.add(instance.reweight(newWeight));
        }
        return trainingDataList;
    }

    private void validateData(Iterable<Instance<Map<String, Serializable>>> trainingData) {
        ClassificationCounter classificationCounter = ClassificationCounter.countAll(trainingData);
        Preconditions.checkArgument(classificationCounter.getCounts().keySet().size() <= 2, "trainingData must contain only 2 classifications, but it had %s", classificationCounter.getCounts().keySet().size());
    }



    @Override
    public void updatePredictiveModel(TemporallyReweightedPM predictiveModel, Iterable<Instance<Map<String, Serializable>>> newData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            validateData(newData);
            DateTime mostRecentInstance = getMostRecentInstance(newData);

            List<Instance<Map<String, Serializable>>> newDataList = reweightTrainingData(newData, mostRecentInstance);

            Classifier pm = predictiveModel.getWrappedClassifier();
            ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(pm, newDataList, splitNodes);
        } else {
            throw new RuntimeException("Cannot update predictive model without UpdatablePredictiveModelBuilder");
        }
    }

    private DateTime getMostRecentInstance(Iterable<? extends Instance> newData) {
        DateTime mostRecent = null;
        for(Instance instance : newData) {
            DateTime instanceTime = dateTimeExtractor.extractDateTime(instance);
            if (mostRecent == null || instanceTime.isAfter(mostRecent)) {
                mostRecent = instanceTime;
            }
        }
        return mostRecent;
    }

    @Override
    public void stripData(TemporallyReweightedPM predictiveModel) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
                ((UpdatablePredictiveModelBuilder) wrappedBuilder).stripData(predictiveModel.getWrappedClassifier());
        } else {
            throw new RuntimeException("Cannot strip data without UpdatablePredictiveModelBuilder");
        }

    }

}
