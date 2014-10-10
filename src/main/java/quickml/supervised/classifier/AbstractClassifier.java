package quickml.supervised.classifier;

import com.google.common.collect.Lists;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 8/17/14.
 */
//where do we want Classifier as a generic type...in downsampling PMB.
public abstract class AbstractClassifier implements Classifier {

 /*   public List<LabelPredictionWeight<PredictionMap>> createLabelPredictionWeights(List<? extends Instance<AttributesMap>> instances, PredictiveModel<AttributesMap, PredictionMap> predictiveModel) {
        List<LabelPredictionWeight<PredictionMap>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<AttributesMap> instance : instances) {
            LabelPredictionWeight<PredictionMap> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(),
                    predictiveModel.predict(instance.getAttributes()), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }
*/
    private static final long serialVersionUID = -5052476771686106526L;
    public double getProbability(AttributesMap attributes, Serializable classification) {
        return predict(attributes).get(classification);
    }
    public abstract PredictionMap predict(AttributesMap attributes);

    public Serializable getClassificationByMaxProb(AttributesMap attributes) {
        PredictionMap predictions = predict(attributes);
        Serializable mostProbableClass = null;
        double probabilityOfMostProbableClass = 0;
        for (Serializable key : predictions.keySet()) {
            if (predictions.get(key).doubleValue() > probabilityOfMostProbableClass) {
                mostProbableClass = key;
                probabilityOfMostProbableClass = predictions.get(key).doubleValue();
            }
        }
        return mostProbableClass;
    }
}
