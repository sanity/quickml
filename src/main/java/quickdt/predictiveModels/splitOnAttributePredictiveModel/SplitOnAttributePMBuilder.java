package quickdt.predictiveModels.splitOnAttributePredictiveModel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.data.Instance;
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
    private final int minimumAmountTotalCrossData;
    private final double percentCrossData;
    private final Set<String> attributeWhiteList;
    private final int minimumAmountCrossDataPerClassification;

    public SplitOnAttributePMBuilder(String attributeKey, PredictiveModelBuilder<?> wrappedBuilder, int minimumAmountCrossData, double percentCrossData, Set<String> attributeWhiteList, int minimumAmountCrossDataPerClassification) {
        this.attributeKey = attributeKey;
        this.wrappedBuilder = wrappedBuilder;
        this.minimumAmountTotalCrossData = minimumAmountCrossData;
        this.percentCrossData = percentCrossData;
        this.attributeWhiteList = attributeWhiteList;
        this.minimumAmountCrossDataPerClassification = minimumAmountCrossDataPerClassification;
    }

    @Override
    public SplitOnAttributePM buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        Map<Serializable, ArrayList<AbstractInstance>> splitTrainingData = splitTrainingData(trainingData);

        Map<Serializable, PredictiveModel> splitModels = Maps.newHashMap();
        for (Map.Entry<Serializable, ArrayList<AbstractInstance>> trainingDataEntry : splitTrainingData.entrySet()) {
            logger.info("Building predictive model for "+attributeKey+"="+trainingDataEntry.getKey());
            setID(trainingDataEntry.getKey());
            splitModels.put(trainingDataEntry.getKey(), wrappedBuilder.buildPredictiveModel(trainingDataEntry.getValue()));
        }

        logger.info("Building default predictive model");
        setID(null);
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
    * selecting that ratio from outside sets. Only keep the attributes in the supporting instances that in in the white list
    * */
    private void crossPollinateData(Map<Serializable, ArrayList<AbstractInstance>> splitTrainingData, ArrayList<AbstractInstance> allData) {
        for(Map.Entry<Serializable, ArrayList<AbstractInstance>> entry : splitTrainingData.entrySet()) {
            ClassificationCounter splitClassificationCounter = ClassificationCounter.countAll(entry.getValue());
            long amountCrossData = (long) Math.max(splitClassificationCounter.getTotal() * percentCrossData, minimumAmountTotalCrossData);
            Set<AbstractInstance> crossData = new HashSet<>();
            ClassificationCounter crossDataCount = new ClassificationCounter();
            for(int i = allData.size()-1; i >= 0; i--) {
                AbstractInstance instance = allData.get(i);
                double classificationRatio = splitClassificationCounter.getCount(instance.getClassification()) / splitClassificationCounter.getTotal();
                double targetCount = Math.max(classificationRatio * amountCrossData, minimumAmountCrossDataPerClassification);
                if(shouldAddInstance(entry.getKey(), instance, crossDataCount, targetCount)) {
                    crossData.add(cleanSupportingData(instance));
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
    private boolean shouldAddInstance(Serializable attributeValue, AbstractInstance instance, ClassificationCounter crossDataCount, double targetCount) {
        if (!attributeValue.equals(instance.getAttributes().get(attributeKey))) {
            if (targetCount > crossDataCount.getCount(instance.getClassification())) {
                return true;
            }
        }
        return false;
    }

    private AbstractInstance cleanSupportingData(AbstractInstance instance) {
        Attributes attributes = new HashMapAttributes();
        for (String key : instance.getAttributes().keySet()) {
            if (attributeWhiteList.isEmpty() || attributeWhiteList.contains(key)) {
                attributes.put(key, instance.getAttributes().get(key));
            }
        }
        return new Instance(attributes, instance.getClassification(), instance.getWeight());
    }

    @Override
    public PredictiveModelBuilder<SplitOnAttributePM> updatable(final boolean updatable) {
        this.wrappedBuilder.updatable(updatable);
        return this;
    }

    @Override
    public void setID(Serializable id) {
        wrappedBuilder.setID(id);
    }

    @Override
    public void updatePredictiveModel(SplitOnAttributePM predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        if (wrappedBuilder instanceof UpdatablePredictiveModelBuilder) {
            Map<Serializable, ArrayList<AbstractInstance>> splitNewData = splitTrainingData(newData);
            for (Map.Entry<Serializable, ArrayList<AbstractInstance>> newDataEntry : splitNewData.entrySet()) {
                PredictiveModel pm = predictiveModel.getSplitModels().get(newDataEntry.getKey());
                if(pm == null) {
                    logger.info("Building predictive model for "+attributeKey+"="+newDataEntry.getKey());
                    setID(newDataEntry.getKey());
                    pm = wrappedBuilder.buildPredictiveModel(newDataEntry.getValue());
                    predictiveModel.getSplitModels().put(newDataEntry.getKey(), pm);
                } else {
                    logger.info("Updating predictive model for "+attributeKey+"="+newDataEntry.getKey());
                    ((UpdatablePredictiveModelBuilder) wrappedBuilder).updatePredictiveModel(pm, newDataEntry.getValue(), trainingData, splitNodes);
                }
            }
            logger.info("Updating default predictive model");
            setID(null);
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
