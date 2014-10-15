package quickml.supervised.classifier.twoStageModel;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.Classifier;

import java.io.Serializable;

/**
 * Created by alexanderhawk on 10/7/14.
 */
public class TwoStageModel extends AbstractClassifier {
/*
    This class wraps 2 binary classifiers, in composite model (which is also a binary classifier) that predicts the probability
    of an instance having positive labels in both situations the wrapped classifiers respectively make predictions for.
 */
    Classifier wrappedOne;
    Classifier wrappedTwo;

    public TwoStageModel(Classifier wrappedOne, Classifier wrappedTwo) {
        this.wrappedOne = wrappedOne;
        this.wrappedTwo = wrappedTwo;
    }

    @Override
    public PredictionMap predict(AttributesMap attributes) {
        PredictionMap predictionMap = PredictionMap.newMap();
        double adjustedPosProb = wrappedOne.getProbability(attributes, 1.0);
        predictionMap.put(1.0, adjustedPosProb);
        predictionMap.put(0.0, 1.0 - adjustedPosProb);
         return predictionMap;
    }


    @Override
    public double getProbability(AttributesMap attributesMap, Serializable label) {
        return wrappedOne.getProbability(attributesMap, label)*wrappedTwo.getProbability(attributesMap, label);
    }

    @Override
    public  void dump(Appendable appendable) {
        wrappedOne.dump(appendable);
        wrappedTwo.dump(appendable);
    }
}
