package quickml.supervised;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.PredictionMap;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.summaryStatistics.ValueCounter;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;
import quickml.supervised.tree.nodes.LeafDepthStats;

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


    public static List<LabelPredictionWeight<Double, Double>> getRegLabelsPredictionsWeights(PredictiveModel<AttributesMap, Double> predictiveModel, List<? extends Instance<AttributesMap, Double>> validationSet) {
        ArrayList<LabelPredictionWeight<Double, Double>> results = new ArrayList<>();
        for (Instance<AttributesMap, Double> instance : validationSet) {
            results.add(new LabelPredictionWeight<Double, Double>(instance.getLabel(), predictiveModel.predict(instance.getAttributes()), instance.getWeight()));
        }
        return results;
    }

    public static PredictionMapResults calcResultPredictions(Classifier predictiveModel, List<? extends InstanceWithAttributesMap<?>> validationSet) {
        ArrayList<PredictionMapResult> results = new ArrayList<>();
        for (InstanceWithAttributesMap<?> instance : validationSet) {
            results.add(new PredictionMapResult(predictiveModel.predict(instance.getAttributes()), instance.getLabel(), instance.getWeight()));
        }
        return new PredictionMapResults(results);
    }

    public static PredictionMapResults calcResultpredictionsWithoutAttrs(Classifier predictiveModel, List<? extends InstanceWithAttributesMap<?>> validationSet, Set<String> attributesToIgnore) {
        ArrayList<PredictionMapResult> results = new ArrayList<>();
        for (InstanceWithAttributesMap<?> instance : validationSet) {
            PredictionMap prediction = predictiveModel.predictWithoutAttributes(instance.getAttributes(), attributesToIgnore);
            results.add(new PredictionMapResult(prediction, instance.getLabel(), instance.getWeight()));
        }
        return new PredictionMapResults(results);
    }
    
    public static <T extends InstanceWithAttributesMap<?>>void sortTrainingInstancesByTime(List<T> trainingData, final DateTimeExtractor<T> dateTimeExtractor) {
        Collections.sort(trainingData, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                DateTime dateTime1 = dateTimeExtractor.extractDateTime(o1);
                DateTime dateTime2 = dateTimeExtractor.extractDateTime(o2);
                return dateTime1.compareTo(dateTime2);
            }
        });
    }



    public static  <T> List<T> iterableToList(Iterable<T> trainingData) {
        if (trainingData instanceof List) {
            return (List<T>) trainingData;
        }
        return Lists.newArrayList(trainingData);
    }

    public static  <T extends InstanceWithAttributesMap<?>> TrueFalsePair<T> setTrueAndFalseTrainingSets(List<T> trainingData, Branch bestNode) {
       /**fly weight pattern */
        int firstIndexOfFalseSet = trainingData.size();
        int trialFirstIndexOfFalseSet = firstIndexOfFalseSet - 1;

        firstIndexOfFalseSet = repartitionTrainingData(trainingData, bestNode, firstIndexOfFalseSet, trialFirstIndexOfFalseSet);
        List<T> trueTrainingSet = trainingData.subList(0, firstIndexOfFalseSet);
        List<T> falseTrainingSet = trainingData.subList(firstIndexOfFalseSet, trainingData.size());
        return new TrueFalsePair(trueTrainingSet, falseTrainingSet);
    }

    public static <T extends InstanceWithAttributesMap<?>> int repartitionTrainingData(List<T> trainingData, Branch bestNode, int firstIndexOfFalseSet, int trialFirstIndexOfFalseSet) {
        for (int i = 0; i < trainingData.size() && firstIndexOfFalseSet > i; i++) {
            T instance = trainingData.get(i);
            if (bestNode.decide(instance.getAttributes())) {
                continue; //the above condition ensures the instance at position i is in the trueSet
            } else {
                //Since we now know the instance is not in true set, we swap with whatever instance sits just before the the firstIndexOfTheFalseSet.  If the new instance at i is in the trueSet,
                //we return to the loop over i.  If not, we decrement firstnIndexOfTheFalseSet, and try swapping again.  We repeat until we either get
                // a trueInstance at position i or we find that the firstIndexOfFalseSet is actually i.
                while (!bestNode.decide(trainingData.get(i).getAttributes()) && (trialFirstIndexOfFalseSet >= i)) {
                    if (i == trialFirstIndexOfFalseSet) { //edge case
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
        return firstIndexOfFalseSet;
    }

    private static <T extends InstanceWithAttributesMap<?>> void swap(int i, int trialFirstIndexOfFalseSet, List<T> trainingData) {
        T temp = trainingData.get(trialFirstIndexOfFalseSet);
        trainingData.set(trialFirstIndexOfFalseSet, trainingData.get(i));
        trainingData.set(i, temp);
    }

    public static class TrueFalsePair<T extends InstanceWithAttributesMap<?>>  {
        public List<T> trueTrainingSet;
        public List<T> falseTrainingSet;

        public TrueFalsePair(List<T> trueTrainingSet, List<T> falseTrainingSet) {
            this.trueTrainingSet = trueTrainingSet;
            this.falseTrainingSet = falseTrainingSet;
        }
    }


    public static <VC extends ValueCounter<VC>>  double meanDepth(Node<VC> node) {
        final LeafDepthStats stats = new LeafDepthStats();
        node.calcLeafDepthStats(stats);
        return (double) stats.ttlDepth / stats.ttlSamples;
    }



}

