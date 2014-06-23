package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.DateTimeExtractor;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;
import quickdt.predictiveModels.splitOnAttributePredictiveModel.SplitOnAttributePM;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedPMBuilder implements UpdatablePredictiveModelBuilder<TemporallyReweightedPM> {
    private static final  Logger logger =  LoggerFactory.getLogger(TemporallyReweightedPMBuilder.class);

    private double halfLifeOfPositive;
    private double decayConstantOfPositive;
    private double halfLifeOfNegative;
    private double decayConstantNegative;
    private PredictiveModelBuilder<?> wrappedBuilder;
    private DateTimeExtractor dateTimeExtractor;
    private Serializable iD;


    public TemporallyReweightedPMBuilder( PredictiveModelBuilder<?> wrappedBuilder, DateTimeExtractor dateTimeExtractor) {
        this.wrappedBuilder = wrappedBuilder;
        this.dateTimeExtractor = dateTimeExtractor;
    }

    public TemporallyReweightedPMBuilder halfLifeOfPositive(double halfLifeOfPositive) {
        this.halfLifeOfPositive = halfLifeOfPositive;
        this.decayConstantOfPositive = halfLifeOfPositive / Math.log(2);
        return this;
    }

    public TemporallyReweightedPMBuilder halfLifeOfNegative(double halfLifeOfNegative) {
        this.halfLifeOfNegative = halfLifeOfNegative;
        this.decayConstantNegative = halfLifeOfNegative / Math.log(2);
        return this;
    }

    //this function should not be in the PMB interface since it is specific to us.
    @Override
    public void setID(Serializable iD) {
        this.iD = iD;
    }

    @Override
    public TemporallyReweightedPM buildPredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        trainingData = reweightTrainingData(trainingData);
        final PredictiveModel predictiveModel = wrappedBuilder.buildPredictiveModel(trainingData);
        return new TemporallyReweightedPM(predictiveModel);
    }

    private ArrayList<AbstractInstance> reweightTrainingData(Iterable<? extends AbstractInstance> trainingData) {
        ArrayList<AbstractInstance> sortedData = sortTrainingData(trainingData);
        applyTemporalReweighting(sortedData);
        return sortedData;
    }

    //refactor to adjust positives and negatives in same pass
    private void applyTemporalReweighting(List<? extends AbstractInstance> sortedData) {
        DateTime mostRecentInstance = dateTimeExtractor.extractDateTime(sortedData.get(sortedData.size()-1));
        for (AbstractInstance instance : sortedData) {
            double decayConstant = (instance.getClassification()==1.0) ? decayConstantOfPositive : decayConstantNegative;
            DateTime timOfInstance = dateTimeExtractor.extractDateTime(instance);
            double hoursBack = Minutes.minutesBetween(mostRecentInstance, timOfInstance).getMinutes();
            double newWeight = instance.getWeight()*Math.exp(-1.0 * hoursBack / decayConstant);
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


    //issues: have to get access to the data stored in the wrapped builder to applyTemporalReweighting it.
    @Override
    public void updatePredictiveModel(TemporallyReweightedPM predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            ArrayList<AbstractInstance> reweightedData = reweightTrainingData(newData);
            ArrayList<AbstractInstance> reweightedTrainingData = reweightTrainingData(trainingData);  //is this needed?

            PredictiveModel pm = predictiveModel.getWrappedModel();
            ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(pm, reweightedTrainingData, trainingData, splitNodes);
            logger.info("Updating default predictive model");
        } else {
            throw new RuntimeException("Cannot update predictive model without UpdatablePredictiveModelBuilder");
        }
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
