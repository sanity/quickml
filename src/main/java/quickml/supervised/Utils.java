package quickml.supervised;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import quickml.data.Instance;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;
import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

import java.util.*;

/**
 * Created by alexanderhawk on 7/31/14.
 */
public class Utils {

    public static <R, L, P> List<LabelPredictionWeight<L, P>> createLabelPredictionWeights(List<? extends Instance> instances, PredictiveModel<R, P> predictiveModel) {
        List<LabelPredictionWeight<L, P>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<R, L> instance : instances) {
            LabelPredictionWeight<L, P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(), predictiveModel.predict(instance.getAttributes()), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }

    public static <R, L, P> List<LabelPredictionWeight<L, P>> createLabelPredictionWeightsWithoutAttributes(List<? extends Instance<R, L>> instances, PredictiveModel<R, P> predictiveModel, Set<String> attributesToIgnore) {
        List<LabelPredictionWeight<L, P>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<R, L> instance : instances) {
            LabelPredictionWeight<L, P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(),
                    predictiveModel.predictWithoutAttributes(instance.getAttributes(), attributesToIgnore), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }


    public static double getInstanceWeights(List<? extends Instance> instances) {
        double weight = 0;
        for (Instance instance : instances) {
            weight += instance.getWeight();
        }
        return weight;
    }

    public static PredictionMapResults calcResultPredictions(Classifier predictiveModel, List<? extends InstanceWithAttributesMap> validationSet) {
        ArrayList<PredictionMapResult> results = new ArrayList<>();
        for (InstanceWithAttributesMap instance : validationSet) {
            results.add(new PredictionMapResult(predictiveModel.predict(instance.getAttributes()), instance.getLabel(), instance.getWeight()));
        }
        return new PredictionMapResults(results);
    }

    public static PredictionMapResults calcResultpredictionsWithoutAttrs(Classifier predictiveModel, List<? extends InstanceWithAttributesMap> validationSet, Set<String> attributesToIgnore) {
        ArrayList<PredictionMapResult> results = new ArrayList<>();
        for (InstanceWithAttributesMap instance : validationSet) {
            PredictionMap prediction = predictiveModel.predictWithoutAttributes(instance.getAttributes(), attributesToIgnore);
            results.add(new PredictionMapResult(prediction, instance.getLabel(), instance.getWeight()));
        }
        return new PredictionMapResults(results);
    }

    public static void sortTrainingInstancesByTime(List<? extends InstanceWithAttributesMap> trainingData, final DateTimeExtractor<InstanceWithAttributesMap> dateTimeExtractor) {
        Collections.sort(trainingData, new Comparator<InstanceWithAttributesMap>() {
            @Override
            public int compare(InstanceWithAttributesMap o1, InstanceWithAttributesMap o2) {
                DateTime dateTime1 = dateTimeExtractor.extractDateTime(o1);
                DateTime dateTime2 = dateTimeExtractor.extractDateTime(o2);
                return dateTime1.compareTo(dateTime2);
            }
        });
    }



    public static  <T> List<T> iterableToList(Iterable<T> trainingData) {
        List<T> trainingDataList = Lists.newArrayList();
        for (T instance : trainingData) {
            trainingDataList.add(instance);
        }
        return trainingDataList;
    }

    public static  <T extends InstanceWithAttributesMap> TrueFalsePair<T> setTrueAndFalseTrainingSets(List<T> trainingData, Branch bestNode) {
        int firstIndexOfFalseSet = trainingData.size();
        int trialFirstIndexOfFalseSet = firstIndexOfFalseSet - 1;

        for (int i = 0; i < trainingData.size() && firstIndexOfFalseSet > i; i++) {
            T instance = trainingData.get(i);
            if (bestNode.decide(instance.getAttributes())) {
                continue; //the above condition ensures the instance at position i in the trueSet
            } else {
                //now swap with whatever instance sits just before the the firstIndexOfTheFalseSet.  If the new instance at i is in the trueSet,
                //we return to the loop over i.  If not, we decrement firstnIndexOfTheFalseSet, and try swapping again.  We repeat until we either get
                // a trueInstance at position i or we find that the firstIndexOfFalseSet is actually i.
                while (!bestNode.decide(trainingData.get(i).getAttributes()) && (trialFirstIndexOfFalseSet >= i)) {
                    if (i == trialFirstIndexOfFalseSet) {
                        firstIndexOfFalseSet = trialFirstIndexOfFalseSet; //we have verified the instance is in the false set by virtue of being in the else block
                        break;
                    }
                    //swap
                    swap(i, trialFirstIndexOfFalseSet, trainingData);
                    firstIndexOfFalseSet = trialFirstIndexOfFalseSet; //the instance we moved into the position indexed by trialFirstIndexOfFalseSet is known to be in the falseSet
                    trialFirstIndexOfFalseSet--;
                }
            }
        }
        List<T> trueTrainingSet = trainingData.subList(0, firstIndexOfFalseSet);
        List<T> falseTrainingSet = trainingData.subList(firstIndexOfFalseSet, trainingData.size());
        return new TrueFalsePair(trueTrainingSet, falseTrainingSet);
    }

    private static <T extends InstanceWithAttributesMap> void swap(int i, int trialFirstIndexOfFalseSet, List<T> trainingData) {
        T temp = trainingData.get(trialFirstIndexOfFalseSet);
        trainingData.set(trialFirstIndexOfFalseSet, trainingData.get(i));
        trainingData.set(i, temp);
    }

    public static class TrueFalsePair<T extends InstanceWithAttributesMap>  {
        public List<T> trueTrainingSet;
        public List<T> falseTrainingSet;

        public TrueFalsePair(List<T> trueTrainingSet, List<T> falseTrainingSet) {
            this.trueTrainingSet = trueTrainingSet;
            this.falseTrainingSet = falseTrainingSet;
        }
    }



}

