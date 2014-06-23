package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Hours;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.DateTimeExtractor;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedPMBuilder implements UpdatablePredictiveModelBuilder<TemporallyReweightedPM> {
    private static final  Logger logger =  LoggerFactory.getLogger(TemporallyReweightedPMBuilder.class);
    public static final double POSTIVE_CLASSIFICATION = 1.0;

    private static final double DEFAULT_DECAY_CONSTANT = 173; //approximately 5 days
    private double decayConstantOfPositive = DEFAULT_DECAY_CONSTANT;
    private double decayConstantOfNegative = DEFAULT_DECAY_CONSTANT;
    private PredictiveModelBuilder<?> wrappedBuilder;
    private DateTimeExtractor dateTimeExtractor;


    public TemporallyReweightedPMBuilder(PredictiveModelBuilder<?> wrappedBuilder, DateTimeExtractor dateTimeExtractor) {
        this.wrappedBuilder = wrappedBuilder;
        this.dateTimeExtractor = dateTimeExtractor;
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
        List<AbstractInstance> trainingDataList = iterableToArrayList(trainingData);

        DateTime mostRecent = dateTimeExtractor.extractDateTime(trainingDataList.get(trainingDataList.size() - 1));
        reweightTrainingData(trainingDataList, mostRecent);
        final PredictiveModel predictiveModel = wrappedBuilder.buildPredictiveModel(trainingDataList);
        return new TemporallyReweightedPM(predictiveModel);
    }

    private void reweightTrainingData(Iterable<? extends AbstractInstance> sortedData, DateTime mostRecentInstance) {
        for (AbstractInstance instance : sortedData) {
            double decayConstant = (instance.getClassification().equals(POSTIVE_CLASSIFICATION)) ? decayConstantOfPositive : decayConstantOfNegative;
            DateTime timOfInstance = dateTimeExtractor.extractDateTime(instance);
            double hoursBack = Hours.hoursBetween(mostRecentInstance, timOfInstance).getHours();
            double newWeight = Math.exp(-1.0 * hoursBack / decayConstant);
            instance.setWeight(newWeight);
        }
    }

    @Override
    public PredictiveModelBuilder<TemporallyReweightedPM> updatable(final boolean updatable) {
        this.wrappedBuilder.updatable(updatable);
        return this;
    }

    @Override
    public void updatePredictiveModel(TemporallyReweightedPM predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            ArrayList<AbstractInstance> trainingDataList = iterableToArrayList(trainingData);
            DateTime mostRecentInstance = getMostRecentInstance(newData);

            reweightTrainingData(trainingDataList, mostRecentInstance);
            reweightTrainingData(newData, mostRecentInstance);

            PredictiveModel pm = predictiveModel.getWrappedModel();
            ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(pm, newData, trainingDataList, splitNodes);
            logger.info("Updating default predictive model");
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

    private ArrayList<AbstractInstance> iterableToArrayList(Iterable<? extends AbstractInstance> trainingData) {
        if (trainingData instanceof ArrayList) {
            return (ArrayList<AbstractInstance>) trainingData;
        }
        ArrayList<AbstractInstance> trainingDataList = Lists.newArrayList();
        for (AbstractInstance instance : trainingData) {
            trainingDataList.add(instance);
        }
        return trainingDataList;
    }

    @Override
    public void stripData(TemporallyReweightedPM predictiveModel) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
                ((UpdatablePredictiveModelBuilder) wrappedBuilder).stripData(predictiveModel.getWrappedModel());
            }
        else {
            throw new RuntimeException("Cannot strip data without UpdatablePredictiveModelBuilder");
        }

    }

}
