package quickml.supervised;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import quickml.data.Instance;
import quickml.data.PredictionMap;
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

    public static void sortTrainingInstancesByTime(List<InstanceWithAttributesMap> trainingData, final DateTimeExtractor<InstanceWithAttributesMap> dateTimeExtractor) {
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



}

