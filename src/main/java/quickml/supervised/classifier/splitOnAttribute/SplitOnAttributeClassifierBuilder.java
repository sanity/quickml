package quickml.supervised.classifier.splitOnAttribute;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.PredictiveModelBuilder;
import quickml.data.instances.InstanceWithAttributesMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.io.Serializable;
import java.util.*;

/**
 * Created by ian on 5/29/14.
 */
public class SplitOnAttributeClassifierBuilder<I extends InstanceWithAttributesMap<?>> implements PredictiveModelBuilder<SplitOnAttributeClassifier, I> {
    private static final Logger logger = LoggerFactory.getLogger(SplitOnAttributeClassifierBuilder.class);
    private final String attributeKey;
    private final PredictiveModelBuilder<? extends Classifier, I> wrappedBuilder;
    private Map<? extends Serializable, Integer> splitValToGroupIdMap;
    private Map<Integer, SplitModelGroup> splitModelGroups;
    private final Integer defaultGroup;

    //TODO:  this method should not have any parameters.
    public SplitOnAttributeClassifierBuilder(String attributeKey, Collection<SplitModelGroup> splitModelGroupsCollection, Integer defaultGroup, PredictiveModelBuilder<? extends Classifier, I> wrappedBuilder) {

        this.attributeKey = attributeKey;
        this.defaultGroup = defaultGroup;
        this.splitModelGroups = getSplitModelGroups(splitModelGroupsCollection);
        this.splitValToGroupIdMap = getSplitValToGroupIdMap(splitModelGroups);
        this.wrappedBuilder = wrappedBuilder;
    }

    @Override
    public SplitOnAttributeClassifier buildPredictiveModel(final Iterable<I> trainingData) {

        //split by groupId
        Map<Integer, List<I>> splitTrainingData = splitTrainingData(trainingData);
        Map<Integer, Classifier> splitModels = Maps.newHashMap();
        for (Map.Entry<Integer, List<I>> trainingDataEntry : splitTrainingData.entrySet()) {
            logger.info("Building predictive model for group" + attributeKey + "=" + trainingDataEntry.getKey());
            splitModels.put(trainingDataEntry.getKey(), wrappedBuilder.buildPredictiveModel(trainingDataEntry.getValue()));
        }

        logger.info("Building default predictive model");

        return new SplitOnAttributeClassifier(attributeKey, splitValToGroupIdMap, defaultGroup, splitModels);
    }

    @Override
    public void updateBuilderConfig(Map<String, Serializable> config) {
        wrappedBuilder.updateBuilderConfig(config);
    }

    public int getGroupFromSplitVal(Serializable val) {
        return splitValToGroupIdMap.get(val);
    }

    private Map<Integer, SplitModelGroup> getSplitModelGroups(Collection<SplitModelGroup> splitModelGroupCollection) {
        Map<Integer, SplitModelGroup> splitModelGroupMap = new HashMap<>();
        for (SplitModelGroup splitModelGroup : splitModelGroupCollection) {
            splitModelGroupMap.put(splitModelGroup.groupId, splitModelGroup);
        }
        return splitModelGroupMap;
    }

    private Map<Serializable, Integer> getSplitValToGroupIdMap(Map<Integer, SplitModelGroup> splitModelGroups) {
        SplitValTGroupIdMap splitValToGroupIdMap = new SplitValTGroupIdMap(defaultGroup);
        for (Integer groupId : splitModelGroups.keySet()) {
            Set<? extends Serializable> valuesOfSplitVariableInTheGroup = splitModelGroups.get(groupId).valuesOfSplitVariableInTheGroup;
            for (Serializable splitVal : valuesOfSplitVariableInTheGroup) {
                splitValToGroupIdMap.put(splitVal, groupId);
            }
        }
        return splitValToGroupIdMap;
    }


    private Map<Integer, List<I>> splitTrainingData(Iterable<I> trainingData) {

        //create lists of data for each split attribute val
        Map<Integer, List<I>> splitTrainingData = Maps.newHashMap();
        for (I instance : trainingData) {
            Serializable value = instance.getAttributes().get(attributeKey);
            Integer groupId;
            if (value != null) {
                groupId = splitValToGroupIdMap.get(value);
            } else {
                continue;
            }

            List<I> trainingDataForGroup = splitTrainingData.get(groupId);
            if (trainingDataForGroup == null) {
                trainingDataForGroup = Lists.newArrayList();
                splitTrainingData.put(groupId, trainingDataForGroup);
            }
            trainingDataForGroup.add(instance);
        }
        //test by walking up to this point with debugger and make sure everything is ok
        //do cross polination
        crossPollinateData(splitTrainingData);
        return splitTrainingData;
    }

    /*
    * Add data to each split data set based on the desired cross data values. Maintain the same ratio of classifications in the split set by
    * selecting that ratio from outside sets. Only keep the attributes in the supporting instances that are in the white list
    * */
    private void crossPollinateData(Map<Integer, List<I>> splitTrainingData) {

        Map<Integer, Long> groupIdToSamplesInTheGroup = new HashMap<>();

        for (Integer groupId : splitTrainingData.keySet()) {
            groupIdToSamplesInTheGroup.put(groupId, (long) splitTrainingData.get(groupId).size());
        }

        for (Integer presentGroup : splitModelGroups.keySet()) {

            List<I> dataForPresentGroup = splitTrainingData.get(presentGroup);
            SplitModelGroup splitModelGroup = splitModelGroups.get(presentGroup);
            Map<Integer, Long> numSamplesFromOtherGroupsMap = splitModelGroup.computeIdealNumberOfSamplesToCollectFromOtherGroups(groupIdToSamplesInTheGroup);
            //for each
            for (Integer crossGroupId : numSamplesFromOtherGroupsMap.keySet()) {
                List<I> instancesFromCrossGroup = splitTrainingData.get(crossGroupId);
                long requestedNumInstances = numSamplesFromOtherGroupsMap.get(crossGroupId);
                List<I> listWithRequestedNumberOfInstancesFromThisCrossGroup = filterToRequestedNumber(instancesFromCrossGroup, requestedNumInstances);
                dataForPresentGroup.addAll(listWithRequestedNumberOfInstancesFromThisCrossGroup);
            }
        }
    }

    private List<I> filterToRequestedNumber(List<I> input, long requestedNumInstances) {
        //TODO: consider allowing it to get the most recently dated instances.

        /**
         * this method obtains a random sublist of approximately m elements from a list of n elements in order m time.
         */

        List<I> output = new ArrayList<>((int) requestedNumInstances);
        double currentSizeToReducedSizeRatio = (1.0 * input.size()) / requestedNumInstances;
        int baseIncrement = (int) Math.floor(currentSizeToReducedSizeRatio);
        double randomIncrementProbability = currentSizeToReducedSizeRatio - baseIncrement;
        int currentIndex = 0;
        Random random = new Random();
        for (int i = 0; i < requestedNumInstances && currentIndex < input.size(); i++) {
            output.add(input.get(currentIndex));
            currentIndex += baseIncrement;
            if (random.nextDouble() < randomIncrementProbability) {
                currentIndex++;
            }
        }
        return output;
    }

    private boolean shouldAddInstance(Serializable attributeValue, I instance, ClassificationCounter crossDataCount, double targetCount) {
        //if the model's split valaue is not the same as the instance's split value (avoids redundancy)
        if (!attributeValue.equals(instance.getAttributes().get(attributeKey))) {
            //if we still need instances of a particular classification
//            if (targetCount > crossDataCount.getCount(instance.getLabel())) {
            return true;
            //          }
        }
        return false;
    }

    public static class SplitModelGroup {
        public final int groupId;
        public final long minTotalSamples;
        public double percentageOfTrainingDataThatIsFromOtherGroups;
        public final Map<Integer, Double> groupIdToPercentageOfCrossDataProvidedMap;
        public final Set<? extends Serializable> valuesOfSplitVariableInTheGroup;

        public SplitModelGroup(int groupId, Set<? extends Serializable> valuesOfSplitVariableInTheGroup, long minTotalSamples, double percentageOfTrainingDataThatIsFromOtherGroups, Map<Integer, Double> relativeImportanceOfEachGroupThatContributesCrossGroupData) {
            this.groupId = groupId;
            this.valuesOfSplitVariableInTheGroup = valuesOfSplitVariableInTheGroup;
            this.minTotalSamples = minTotalSamples;
            this.percentageOfTrainingDataThatIsFromOtherGroups = percentageOfTrainingDataThatIsFromOtherGroups;
            this.groupIdToPercentageOfCrossDataProvidedMap = relativeImportanceOfEachGroupThatContributesCrossGroupData;
        }

        public Map<Integer, Long> computeIdealNumberOfSamplesToCollectFromOtherGroups(Map<Integer, Long> groupIdToSamplesInTheGroup) {
            Map<Integer, Long> numberOfSamplesToCollectFromGroups = new HashMap<>();

            long numNonCrossTrainingDataSamples = groupIdToSamplesInTheGroup.get(groupId);
            double percentageOfNonCrossTrainingData = 1 - percentageOfTrainingDataThatIsFromOtherGroups;

            boolean cannotAchieveSpecifiedPercentageOfTrainingDataThatIsFromOtherGroups = minTotalSamples > numNonCrossTrainingDataSamples / percentageOfNonCrossTrainingData;
            long numCrossPolinatedInstancesNeeded;
            if (cannotAchieveSpecifiedPercentageOfTrainingDataThatIsFromOtherGroups) {
                numCrossPolinatedInstancesNeeded = minTotalSamples - numNonCrossTrainingDataSamples;
            } else {
                numCrossPolinatedInstancesNeeded = (long) Math.ceil(numNonCrossTrainingDataSamples * (1 - percentageOfNonCrossTrainingData) / percentageOfNonCrossTrainingData);
            }

            for (Integer groupId : groupIdToPercentageOfCrossDataProvidedMap.keySet()) {
                numberOfSamplesToCollectFromGroups.put(groupId, (long) (groupIdToPercentageOfCrossDataProvidedMap.get(groupId) * numCrossPolinatedInstancesNeeded));
            }
            //TODO: compare lengths in numberOfSamplesToCollectFromGroups to the actual numberOfTraining examples of the other groups, and intelligently rebalance the requsted numbers based on what is actually possible.
            // For the time being, if one group  has fewer actual instances than it is requested to provide, just provide all of it's training instances.

            return numberOfSamplesToCollectFromGroups;
        }
    }
}
