package quickml.supervised.crossValidation.crossValLossFunctions;

import com.google.common.collect.Lists;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.downsampling.Utils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 10/23/14.
 */
public class LossFunctionCorrectedForDownsampling {
    CrossValLossFunction<PredictionMap> wrappedLossFunction;
    CorrectionFunction correctionFunction;

    LossFunctionCorrectedForDownsampling(CrossValLossFunction<PredictionMap> wrappedLossFunction, CorrectionFunction correctionFunction) {
        this.correctionFunction = correctionFunction;
        this.wrappedLossFunction = wrappedLossFunction;
    }

    LossFunctionCorrectedForDownsampling(CrossValLossFunction<PredictionMap> wrappedLossFunction, double dropProbability, Serializable negativeLabel) {
        this.correctionFunction = new NegativeInstanceCorrectionFunction(negativeLabel, dropProbability);
        this.wrappedLossFunction = wrappedLossFunction;
    }

    public List<LabelPredictionWeight<PredictionMap>> correctLabelPredictionWeights(List<LabelPredictionWeight<PredictionMap>> uncorrectedLabelPredictionsWeights) {
        List<LabelPredictionWeight<PredictionMap>> labelPredictionWeights = Lists.newArrayList();
        for (LabelPredictionWeight<PredictionMap> uncorrectedLabelPredictionWeight : uncorrectedLabelPredictionsWeights) {
            labelPredictionWeights.add(correctionFunction.getCorrectedLabelPredictionWeight(uncorrectedLabelPredictionWeight));
        }
        return labelPredictionWeights;
    }

    interface CorrectionFunction {
        LabelPredictionWeight<PredictionMap> getCorrectedLabelPredictionWeight(LabelPredictionWeight<PredictionMap> labelPredictionWeight);
    }

    class NegativeInstanceCorrectionFunction implements CorrectionFunction {
        /* This class assumes instances have positive or negative instances
         */
        Serializable negativeLabel = Double.valueOf(0.0);
        double dropProbability;

        NegativeInstanceCorrectionFunction(Serializable negativeLabel, double dropProbability) {
            this.negativeLabel = negativeLabel;
            this.dropProbability = dropProbability;
        }

        NegativeInstanceCorrectionFunction(double dropProbability) {
            this.dropProbability = dropProbability;
        }

        @Override
        public LabelPredictionWeight<PredictionMap> getCorrectedLabelPredictionWeight(LabelPredictionWeight<PredictionMap> labelPredictionWeight) {
            PredictionMap correctedPredictionMap = PredictionMap.newMap();
            PredictionMap uncorrectedPrediction = labelPredictionWeight.getPrediction();
            double correctedProbability;
            for (Serializable key : uncorrectedPrediction.keySet()) {
                if (key.equals(negativeLabel)) {
                    correctedProbability = 1.0 - Utils.correctProbability(dropProbability, uncorrectedPrediction.get(key));
                    correctedPredictionMap.put(key, correctedProbability);
                } else {
                    correctedProbability = Utils.correctProbability(dropProbability, uncorrectedPrediction.get(key));
                    correctedPredictionMap.put(key, correctedProbability);
                }
            }
            double correctedWeight = labelPredictionWeight.getWeight();
            if (labelPredictionWeight.getLabel().equals(negativeLabel))
                correctedWeight/=dropProbability;
            return new LabelPredictionWeight<PredictionMap>(labelPredictionWeight.label, correctedPredictionMap, correctedWeight);
        }

    }
}
