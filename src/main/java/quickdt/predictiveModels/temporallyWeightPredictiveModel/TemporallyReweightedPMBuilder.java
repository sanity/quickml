package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
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

    private double halfLifeOfPositiveInHours;
    private double decayConstantOfPositive;
    private double halfLifeOfNegativeInHours;
    private double decayConstantNegative;
    private PredictiveModelBuilder<?> wrappedBuilder;
    private DateTimeExtractor dateTimeExtractor;
    private Serializable iD;


    public TemporallyReweightedPMBuilder( PredictiveModelBuilder<?> wrappedBuilder, DateTimeExtractor dateTimeExtractor) {
        this.wrappedBuilder = wrappedBuilder;
        this.dateTimeExtractor = dateTimeExtractor;
    }

    public TemporallyReweightedPMBuilder halfLifeOfPositive(double halfLifeOfPositiveInDays) {
        this.halfLifeOfPositiveInHours = halfLifeOfPositiveInDays*24;
        this.decayConstantOfPositive = halfLifeOfPositiveInHours / Math.log(2);
        return this;
    }

    public TemporallyReweightedPMBuilder halfLifeOfNegative(double halfLifeOfNegativeInDays) {
        this.halfLifeOfNegativeInHours = halfLifeOfNegativeInDays;
        this.decayConstantNegative = halfLifeOfNegativeInHours / Math.log(2);
        return this;
    }

    //this function should not be in the PMB interface since it is specific to us.
    @Override
    public void setID(Serializable iD) {
        this.iD = iD;
    }

    @Override
    public TemporallyReweightedPM buildPredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        ArrayList<AbstractInstance> trainingDataList = Lists.newArrayList();
        for (AbstractInstance instance : trainingData)
            trainingDataList.add(instance);
        DateTime mostRecent = dateTimeExtractor.extractDateTime(trainingDataList.get(trainingDataList.size() - 1));
        trainingDataList = reweightTrainingData(trainingDataList, mostRecent);
        final PredictiveModel predictiveModel = wrappedBuilder.buildPredictiveModel(trainingData);
        return new TemporallyReweightedPM(predictiveModel);
    }

    private ArrayList<AbstractInstance> reweightTrainingData(ArrayList<AbstractInstance> sortedData, DateTime mostRecentInstance) {
        applyTemporalReweighting(sortedData, mostRecentInstance);
        return sortedData;
    }


    //refactor to adjust positives and negatives in same pass
    private void applyTemporalReweighting(List<? extends AbstractInstance> sortedData, DateTime mostRecentInstance) {
        for (AbstractInstance instance : sortedData) {
            double decayConstant = (instance.getClassification()==1.0) ? decayConstantOfPositive : decayConstantNegative;
            DateTime timOfInstance = dateTimeExtractor.extractDateTime(instance);
            double hoursBack = Hours.hoursBetween(mostRecentInstance, timOfInstance).getHours();
            double newWeight = Math.exp(-1.0 * hoursBack / decayConstant);
            instance.setWeight(newWeight);
        }
    }

    private ArrayList<AbstractInstance> sortTrainingData(Iterable<? extends AbstractInstance> trainingData) {
        List<AbstractInstance> sortedData = Lists.<AbstractInstance>newArrayList();
        for (AbstractInstance instance : trainingData) {
            sortedData.add(instance);
        }

        Comparator<AbstractInstance> comparator = new Comparator<AbstractInstance>() {
            @Override
            public int compare(AbstractInstance o1, AbstractInstance o2) {
                DateTime firstInstance = dateTimeExtractor.extractDateTime(o1);
                DateTime secondInstance = dateTimeExtractor.extractDateTime(o2);
                if (firstInstance.isAfter(secondInstance)) {
                    return 1;
                } else if (firstInstance.isEqual(secondInstance)) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };

        Collections.sort(sortedData, comparator);
        return (ArrayList<AbstractInstance>)sortedData;
}

    @Override
    public PredictiveModelBuilder<TemporallyReweightedPM> updatable(final boolean updatable) {
        this.wrappedBuilder.updatable(updatable);
        return this;
    }


    @Override
    public void updatePredictiveModel(TemporallyReweightedPM predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            ArrayList<AbstractInstance> trainingDataList = IterableToArrayList(trainingData);
            ArrayList<AbstractInstance> sortedNewData = sortTrainingData(newData); //don't need to sort...just get max element
            DateTime mostRecentInstance = dateTimeExtractor.extractDateTime(sortedNewData.get(sortedNewData.size()-1));

            ArrayList<AbstractInstance> reweightedTrainingData = reweightTrainingData(trainingDataList, mostRecentInstance);  //is this needed?
            ArrayList<AbstractInstance> reweightedNewTrainingData = reweightTrainingData(sortedNewData, mostRecentInstance);  //is this needed?

            PredictiveModel pm = predictiveModel.getWrappedModel();
            ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(pm, reweightedNewTrainingData, reweightedTrainingData, splitNodes);
            logger.info("Updating default predictive model");
        } else {
            throw new RuntimeException("Cannot update predictive model without UpdatablePredictiveModelBuilder");
        }
    }

    private ArrayList<AbstractInstance> IterableToArrayList(List<? extends AbstractInstance> trainingData) {
        ArrayList<AbstractInstance> trainingDataList = Lists.newArrayList();
        for (AbstractInstance instance : trainingData)
            trainingDataList.add(instance);
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
