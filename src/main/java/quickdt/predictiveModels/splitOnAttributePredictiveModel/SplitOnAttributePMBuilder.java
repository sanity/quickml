package quickdt.predictiveModels.splitOnAttributePredictiveModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ian on 5/29/14.
 */
public class SplitOnAttributePMBuilder implements UpdatablePredictiveModelBuilder<SplitOnAttributePM> {
    private static final  Logger logger =  LoggerFactory.getLogger(SplitOnAttributePMBuilder.class);

    public static final Double NO_VALUE_PLACEHOLDER = Double.MIN_VALUE;

    private final String attributeKey;
    private final PredictiveModelBuilder<?> wrappedBuilder;

    public SplitOnAttributePMBuilder(String attributeKey, PredictiveModelBuilder<?> wrappedBuilder) {
        this.attributeKey = attributeKey;
        this.wrappedBuilder = wrappedBuilder;
    }

    @Override
    public SplitOnAttributePM buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        Map<Serializable, ArrayList<AbstractInstance>> splitTrainingData = splitTrainingData(trainingData);

        Map<Serializable, PredictiveModel> splitModels = Maps.newHashMap();
        for (Map.Entry<Serializable, ArrayList<AbstractInstance>> trainingDataEntry : splitTrainingData.entrySet()) {
            logger.info("Building predictive model for "+attributeKey+"="+trainingDataEntry.getKey());
            splitModels.put(trainingDataEntry.getKey(), wrappedBuilder.buildPredictiveModel(trainingDataEntry.getValue()));
        }

        logger.info("Building default predictive model");
        final PredictiveModel defaultPM = wrappedBuilder.buildPredictiveModel(trainingData);
        return new SplitOnAttributePM(attributeKey, splitModels, defaultPM);
    }

    private Map<Serializable, ArrayList<AbstractInstance>> splitTrainingData(Iterable<? extends AbstractInstance> trainingData) {
        Map<Serializable, ArrayList<AbstractInstance>> splitTrainingData = Maps.newHashMap();
        for (AbstractInstance instance : trainingData) {
            Serializable value = instance.getAttributes().get(attributeKey);
            if (value == null) value = NO_VALUE_PLACEHOLDER;
            ArrayList<AbstractInstance> splitData = splitTrainingData.get(value);
            if (splitData == null) {
                splitData = Lists.newArrayList();
                splitTrainingData.put(value, splitData);
            }
            splitData.add(instance);
        }
        return splitTrainingData;
    }

    @Override
    public PredictiveModelBuilder<SplitOnAttributePM> updatable(final boolean updatable) {
        this.wrappedBuilder.updatable(updatable);
        return this;
    }

    @Override
    public void updatePredictiveModel(SplitOnAttributePM predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            Map<Serializable, ArrayList<AbstractInstance>> splitNewData = splitTrainingData(newData);
            for (Map.Entry<Serializable, ArrayList<AbstractInstance>> newDataEntry : splitNewData.entrySet()) {
                PredictiveModel pm = predictiveModel.getSplitModels().get(newDataEntry.getKey());
                if(pm == null) {
                    logger.info("Building predictive model for "+attributeKey+"="+newDataEntry.getKey());
                    pm = wrappedBuilder.buildPredictiveModel(newDataEntry.getValue());
                    predictiveModel.getSplitModels().put(newDataEntry.getKey(), pm);
                } else {
                    logger.info("Updating predictive model for "+attributeKey+"="+newDataEntry.getKey());
                    ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(pm, newDataEntry.getValue(), trainingData, splitNodes);
                }
            }
            logger.info("Updating default predictive model");
            ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(predictiveModel.getDefaultPM(), newData, trainingData, splitNodes);
        } else {
            throw new RuntimeException("Cannot update predictive model without UpdatablePredictiveModelBuilder");
        }
    }

    @Override
    public void stripData(SplitOnAttributePM predictiveModel) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            for(PredictiveModel pm : predictiveModel.getSplitModels().values()) {
                ((UpdatablePredictiveModelBuilder) wrappedBuilder).stripData(pm);
            }
            ((UpdatablePredictiveModelBuilder) wrappedBuilder).stripData(predictiveModel.getDefaultPM());
        } else {
            throw new RuntimeException("Cannot strip data without UpdatablePredictiveModelBuilder");
        }
    }
}
