package quickml.supervised;

import com.google.common.collect.Lists;
import quickml.data.Instance;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public static PredictionMapResults calcResultPredictions(Classifier predictiveModel, List<? extends ClassifierInstance> validationSet) {
        ArrayList<PredictionMapResult> results = new ArrayList<>();
        for (ClassifierInstance instance : validationSet) {
            results.add(new PredictionMapResult(predictiveModel.predict(instance.getAttributes()), instance.getLabel(), instance.getWeight()));
        }
        return new PredictionMapResults(results);
    }

    public static PredictionMapResults calcResultpredictionsWithoutAttrs(Classifier predictiveModel, List<? extends ClassifierInstance> validationSet, Set<String> attributesToIgnore) {
        ArrayList<PredictionMapResult> results = new ArrayList<>();
        for (ClassifierInstance instance : validationSet) {
            PredictionMap prediction = predictiveModel.predictWithoutAttributes(instance.getAttributes(), attributesToIgnore);
            results.add(new PredictionMapResult(prediction, instance.getLabel(), instance.getWeight()));
        }
        return new PredictionMapResults(results);
    }


}

