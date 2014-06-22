package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.mahout.classifier.df.builder.DecisionTreeBuilder;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.DateTimeExtractor;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.tree.ClassificationCounter;
import quickdt.predictiveModels.splitOnAttributePredictiveModel.SplitOnAttributePM;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class TemporallyReweightedPMBuilder implements UpdatablePredictiveModelBuilder<TemporallyReweightedPM> {
    private static final  Logger logger =  LoggerFactory.getLogger(TemporallyReweightedPMBuilder.class);

    private double clickcHalfLife;
    private double clickDecayConstant;
    private double nonClickcHalfLife;
    private double nonClickDecayConstant;
    private PredictiveModelBuilder<?> wrappedBuilder;
    private DateTimeExtractor dateTimeExtractor;


    public TemporallyReweightedPMBuilder( PredictiveModelBuilder<?> wrappedBuilder, DateTimeExtractor dateTimeExtractor) {
        this.wrappedBuilder = wrappedBuilder;
        this.dateTimeExtractor = dateTimeExtractor;
    }

    public TemporallyReweightedPMBuilder clickHalfLife(double clickcHalfLife) {
        this.clickcHalfLife = clickcHalfLife;
        this.clickDecayConstant = clickcHalfLife / Math.log(2);
        return this;
    }

    public TemporallyReweightedPMBuilder nonClickHalfLife(double nonClickcHalfLife) {
        this.nonClickcHalfLife = nonClickcHalfLife;
        this.nonClickDecayConstant = nonClickcHalfLife / Math.log(2);
        return this;
    }

    @Override
    public TemporallyReweightedPM buildPredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        trainingData = reweightTrainingData(trainingData);
        final PredictiveModel predictiveModel = wrappedBuilder.buildPredictiveModel(trainingData);
        return new TemporallyReweightedPM(predictiveModel);
    }

    private ArrayList<AbstractInstance> reweightTrainingData(Iterable<? extends AbstractInstance> trainingData) {

        ArrayList<AbstractInstance> sortedData = sortTrainingData(trainingData);
        reweight(sortedData, "positive");
        reweight(sortedData, "negative");
        return sortedData;
    }

    //refactor to adjust positives and negatives in same pass
    private void reweight(List<? extends AbstractInstance> sortedData, String eventType) {
        DateTime mostRecentInstance = dateTimeExtractor.extractDateTime(sortedData.get(sortedData.size()-1));
        double eventClass = (eventType.equals("positive")) ? 1.0 : 0.0;
        double decayConstant = (eventType.equals("positive")) ? clickDecayConstant : nonClickDecayConstant;
        for (AbstractInstance instance : sortedData) {
            if (instance.getClassification()==eventClass){
                DateTime timOfInstance = dateTimeExtractor.extractDateTime(instance);
                double hoursBack = Minutes.minutesBetween(mostRecentInstance, timOfInstance).getMinutes();
                double newWeight = instance.getWeight()*Math.exp(-1.0 * hoursBack / decayConstant);
                instance.setWeight(newWeight);
            }
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


    //issues: have to get access to the data stored in the wrapped builder to reweight it.
    @Override
    public void updatePredictiveModel(TemporallyReweightedPM predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            ArrayList<AbstractInstance> reweightedData = reweightTrainingData(newData);
            ArrayList<AbstractInstance> reweightedTrainingData = reweightTrainingData(trainingData);  //is this needed?

            PredictiveModel pm = predictiveModel.getWrappedModel();
            ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(pm, reweightedTrainingData, trainingData, splitNodes);
            logger.info("Updating default predictive model");
        }
        else {
            throw new RuntimeException("Cannot update predictive model without UpdatablePredictiveModelBuilder");
        }
    }

}
