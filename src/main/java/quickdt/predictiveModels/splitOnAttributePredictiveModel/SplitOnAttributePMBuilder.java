package quickdt.predictiveModels.splitOnAttributePredictiveModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class SplitOnAttributePMBuilder implements UpdatablePredictiveModelBuilder<SplitOnAttributePM> {
    private static final  Logger logger =  LoggerFactory.getLogger(SplitOnAttributePMBuilder.class);

    public static final Double NO_VALUE_PLACEHOLDER = Double.MIN_VALUE;

    private final String attributeKey;
    private final PredictiveModelBuilder<?> wrappedBuilder;
    private final long minimumAmountCrossData;
    private final double percentCrossData;

    public SplitOnAttributePMBuilder(String attributeKey, PredictiveModelBuilder<?> wrappedBuilder, long minimumAmountCrossData, double percentCrossData) {
        this.attributeKey = attributeKey;
        this.wrappedBuilder = wrappedBuilder;
        this.minimumAmountCrossData = minimumAmountCrossData;
        this.percentCrossData = percentCrossData;
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
        ArrayList<AbstractInstance> allData = new ArrayList<>();
        for (AbstractInstance instance : trainingData) {
            Serializable value = instance.getAttributes().get(attributeKey);
            if (value == null) value = NO_VALUE_PLACEHOLDER;
            ArrayList<AbstractInstance> splitData = splitTrainingData.get(value);
            if (splitData == null) {
                splitData = Lists.newArrayList();
                splitTrainingData.put(value, splitData);
            }
            splitData.add(instance);
            allData.add(instance);
        }

        crossPollinateData(splitTrainingData, allData);
        return splitTrainingData;
    }

    /*
    * Add data to each split data set based on the desired cross data values. Maintain the same ratio of classifications in the split set by
    * selecting that ratio from outside sets
    * */
    private void crossPollinateData(Map<Serializable, ArrayList<AbstractInstance>> splitTrainingData, ArrayList<AbstractInstance> allData) {
        for(Map.Entry<Serializable, ArrayList<AbstractInstance>> entry : splitTrainingData.entrySet()) {
            ClassificationCounter classificationCounter = ClassificationCounter.countAll(entry.getValue());
            long amountCrossData = (long) Math.max(classificationCounter.getTotal() * percentCrossData, minimumAmountCrossData);
            Set<AbstractInstance> crossData = new HashSet<>();
            ClassificationCounter crossDataCount = new ClassificationCounter();
            for(int i = allData.size()-1; i >= 0; i--) {
                AbstractInstance instance = allData.get(i);
                if(shouldAddInstance(entry.getKey(), instance, classificationCounter, crossDataCount, amountCrossData)) {
                    crossData.add(instance);
                    crossDataCount.addClassification(instance.getClassification(), instance.getWeight());
                }
                if(crossDataCount.getTotal() >= amountCrossData) {
                    break;
                }
            }
            //cross pollinate data
            entry.getValue().addAll(crossData);
        }
    }

    /*
     * Add instances such that the ratio of classifications is unchanged
    * */
    private boolean shouldAddInstance(Serializable attributeValue, AbstractInstance instance, ClassificationCounter classificationCounter, ClassificationCounter crossDataCount, long amountCrossData) {
        if (!instance.getAttributes().get(attributeKey).equals(attributeValue)) {
            double targetCount = classificationCounter.getCount(instance.getClassification()) / classificationCounter.getTotal() * amountCrossData;
            if (targetCount > crossDataCount.getCount(instance.getClassification())) {
                return true;
            }
        }
        return false;
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
