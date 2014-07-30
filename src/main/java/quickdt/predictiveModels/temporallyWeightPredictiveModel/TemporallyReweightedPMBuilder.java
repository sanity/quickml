package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Hours;
import quickdt.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.tree.ClassificationCounter;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedPMBuilder implements UpdatablePredictiveModelBuilder<TemporallyReweightedPM> {
    private static final double DEFAULT_DECAY_CONSTANT = 173; //approximately 5 days
    private double decayConstantOfPositive = DEFAULT_DECAY_CONSTANT;
    private double decayConstantOfNegative = DEFAULT_DECAY_CONSTANT;
    private final PredictiveModelBuilder<?> wrappedBuilder;
    private final DateTimeExtractor dateTimeExtractor;
    private final Serializable positiveClassification;

    public TemporallyReweightedPMBuilder(PredictiveModelBuilder<?> wrappedBuilder, DateTimeExtractor dateTimeExtractor) {
        this(wrappedBuilder, dateTimeExtractor, 1.0);
    }

    public TemporallyReweightedPMBuilder(PredictiveModelBuilder<?> wrappedBuilder, DateTimeExtractor dateTimeExtractor, Serializable positiveClassification) {
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
    public TemporallyReweightedPM buildPredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        validateData(trainingData);
        DateTime mostRecent = getMostRecentInstance(trainingData);
        List<AbstractInstance> trainingDataList = reweightTrainingData(trainingData, mostRecent);
        final PredictiveModel<Object> predictiveModel = wrappedBuilder.buildPredictiveModel(trainingDataList);
        return new TemporallyReweightedPM(predictiveModel);
    }

    private List<AbstractInstance> reweightTrainingData(Iterable<? extends AbstractInstance> sortedData, DateTime mostRecentInstance) {
        ArrayList<AbstractInstance> trainingDataList = Lists.newArrayList();
        for (AbstractInstance instance : sortedData) {
            double decayConstant = (instance.getObserveredValue().equals(positiveClassification)) ? decayConstantOfPositive : decayConstantOfNegative;
            DateTime timeOfInstance = dateTimeExtractor.extractDateTime(instance);
            double hoursBack = Hours.hoursBetween(mostRecentInstance, timeOfInstance).getHours();
            double newWeight = Math.exp(-1.0 * hoursBack / decayConstant);
            trainingDataList.add(instance.reweight(newWeight));
        }
        return trainingDataList;
    }

    private void validateData(Iterable<? extends AbstractInstance> trainingData) {
        ClassificationCounter classificationCounter = ClassificationCounter.countAll(trainingData);
        Preconditions.checkArgument(classificationCounter.getCounts().keySet().size() <= 2, "trainingData must contain only 2 classifications, but it had %s", classificationCounter.getCounts().keySet().size());
    }

    @Override
    public PredictiveModelBuilder<TemporallyReweightedPM> updatable(final boolean updatable) {
        this.wrappedBuilder.updatable(updatable);
        return this;
    }

    @Override
    public void updatePredictiveModel(TemporallyReweightedPM predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            validateData(newData);
            DateTime mostRecentInstance = getMostRecentInstance(newData);

            List<AbstractInstance> trainingDataList = reweightTrainingData(trainingData, mostRecentInstance);
            List<AbstractInstance> newDataList = reweightTrainingData(newData, mostRecentInstance);

            PredictiveModel<Object> pm = predictiveModel.getWrappedModel();
            ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(pm, newDataList, trainingDataList, splitNodes);
        } else {
            throw new RuntimeException("Cannot update predictive model without UpdatablePredictiveModelBuilder");
        }
    }

    private DateTime getMostRecentInstance(Iterable<? extends AbstractInstance> newData) {
        DateTime mostRecent = null;
        for(AbstractInstance instance : newData) {
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
                ((UpdatablePredictiveModelBuilder) wrappedBuilder).stripData(predictiveModel.getWrappedModel());
        } else {
            throw new RuntimeException("Cannot strip data without UpdatablePredictiveModelBuilder");
        }

    }

}
