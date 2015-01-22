package quickml.supervised.classifier.splitOnAttribute;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.decisionTree.tree.ClassificationCounter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ian on 5/29/14.
 */
public class SplitOnAttributeClassifierBuilder implements PredictiveModelBuilder<AttributesMap, SplitOnAttributeClassifier> {
    private static final  Logger logger =  LoggerFactory.getLogger(SplitOnAttributeClassifierBuilder.class);

    public static final Double NO_VALUE_PLACEHOLDER = Double.MIN_VALUE;

    private final String attributeKey;
    private final PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedBuilder;

    private final int minimumAmountTotalCrossData;
    private final double percentCrossData;
    private final Set<String> attributeWhiteList;
    private final int minimumAmountCrossDataPerClassification;

    public SplitOnAttributeClassifierBuilder(String attributeKey, PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedBuilder, int minimumAmountCrossData, double percentCrossData, Set<String> attributeWhiteList, int minimumAmountCrossDataPerClassification) {

        this.attributeKey = attributeKey;
        this.wrappedBuilder = wrappedBuilder;
        this.minimumAmountTotalCrossData = minimumAmountCrossData;
        this.percentCrossData = percentCrossData;
        this.attributeWhiteList = attributeWhiteList;
        this.minimumAmountCrossDataPerClassification = minimumAmountCrossDataPerClassification;
    }

    @Override
    public SplitOnAttributeClassifier buildPredictiveModel(final Iterable<? extends Instance<AttributesMap>> trainingData) {
        Map<Serializable, ArrayList<Instance<AttributesMap>>> splitTrainingData = splitTrainingData(trainingData);

        Map<Serializable, Classifier> splitModels = Maps.newHashMap();
        for (Map.Entry<Serializable, ArrayList<Instance<AttributesMap>>> trainingDataEntry : splitTrainingData.entrySet()) {
            logger.info("Building predictive model for "+attributeKey+"="+trainingDataEntry.getKey());
            splitModels.put(trainingDataEntry.getKey(), wrappedBuilder.buildPredictiveModel(trainingDataEntry.getValue()));
        }

        logger.info("Building default predictive model");
        final Classifier defaultPM = wrappedBuilder.buildPredictiveModel(trainingData);
        return new SplitOnAttributeClassifier(attributeKey, splitModels, defaultPM);
    }


    private Map<Serializable, ArrayList<Instance<AttributesMap>>> splitTrainingData(Iterable<? extends Instance<AttributesMap>> trainingData) {

       //create lists of data for each split attribute val
        Map<Serializable, ArrayList<Instance<AttributesMap>>> splitTrainingData = Maps.newHashMap();
        ArrayList<Instance<AttributesMap>> allData = new ArrayList<>();
        for (Instance<AttributesMap> instance : trainingData) {
            Serializable value = instance.getAttributes().get(attributeKey);
            if (value == null) value = NO_VALUE_PLACEHOLDER;
            ArrayList<Instance<AttributesMap>> splitData = splitTrainingData.get(value);
            if (splitData == null) {
                splitData = Lists.newArrayList();
                splitTrainingData.put(value, splitData);
            }
            splitData.add(instance);
            allData.add(instance);
        }
        //do cross polination
        crossPollinateData(splitTrainingData, allData);
        return splitTrainingData;
    }

    /*
    * Add data to each split data set based on the desired cross data values. Maintain the same ratio of classifications in the split set by
    * selecting that ratio from outside sets. Only keep the attributes in the supporting instances that are in the white list
    * */
    private void crossPollinateData(Map<Serializable, ArrayList<Instance<AttributesMap>>> splitTrainingData, ArrayList<Instance<AttributesMap>> allData) {
        for(Map.Entry<Serializable, ArrayList<Instance<AttributesMap>>> entry : splitTrainingData.entrySet()) {
            ClassificationCounter splitClassificationCounter = ClassificationCounter.countAll(entry.getValue()); //counts training instances associated with each split value (by classification and total)
            long amountCrossData = (long) Math.max(splitClassificationCounter.getTotal() * percentCrossData, minimumAmountTotalCrossData);  //gets number of cross training instances to add
            Set<Instance<AttributesMap>> crossData = new HashSet<>();
            ClassificationCounter crossDataCount = new ClassificationCounter();
            for(int i = allData.size()-1; i >= 0; i--) {
                Instance<AttributesMap> instance = allData.get(i);
                double classificationRatio = splitClassificationCounter.getCount(instance.getLabel()) / splitClassificationCounter.getTotal(); //fraction of data by classification type in the un cross polinated data set
                double targetCountByClassification = Math.max(classificationRatio * amountCrossData, minimumAmountCrossDataPerClassification); //number of instances to add of a particular classification
                if(shouldAddInstance(entry.getKey(), instance, crossDataCount, targetCountByClassification)) {
                    crossData.add(cleanSupportingData(instance));
                    crossDataCount.addClassification(instance.getLabel(), instance.getWeight());  //updates the amount of instances we have added by classification
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
    private boolean shouldAddInstance(Serializable attributeValue, Instance<AttributesMap> instance, ClassificationCounter crossDataCount, double targetCount) {
        //if the model's split valaue is not the same as the instance's split value (avoids redundancy)
        if (!attributeValue.equals(instance.getAttributes().get(attributeKey))) {
            //if we still need instances of a particular classification
//            if (targetCount > crossDataCount.getCount(instance.getLabel())) {
                return true;
  //          }
        }
        return false;
    }

    private Instance<AttributesMap>cleanSupportingData(Instance<AttributesMap> instance) {
        AttributesMap attributes = AttributesMap.newHashMap();
        for (String key : instance.getAttributes().keySet()) {
            if (attributeWhiteList.isEmpty() || attributeWhiteList.contains(key)) {
                attributes.put(key, instance.getAttributes().get(key));
            }
        }
        return new InstanceImpl(attributes, instance.getLabel(), instance.getWeight());
    }

}
