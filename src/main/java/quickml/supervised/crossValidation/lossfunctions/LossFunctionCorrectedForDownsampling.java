package quickml.supervised.crossValidation.lossfunctions;

import com.google.common.collect.Lists;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.supervised.classifier.downsampling.DownsamplingUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 10/23/14.
 */
public class LossFunctionCorrectedForDownsampling extends ClassifierLossFunction {
    ClassifierLossFunction wrappedLossFunction;
    CorrectionFunction correctionFunction;

    public LossFunctionCorrectedForDownsampling(ClassifierLossFunction wrappedLossFunction, CorrectionFunction correctionFunction) {
        this.correctionFunction = correctionFunction;
        this.wrappedLossFunction = wrappedLossFunction;
    }

    public LossFunctionCorrectedForDownsampling(ClassifierLossFunction wrappedLossFunction, double dropProbability, Serializable negativeLabel) {
        this.correctionFunction = new NegativeInstanceCorrectionFunction(negativeLabel, dropProbability);
        this.wrappedLossFunction = wrappedLossFunction;
    }

    @Override
    public Double getLoss(PredictionMapResults results) {
        PredictionMapResults correctedLabelPredictionWeights = correctLabelPredictionWeights(results);
        return wrappedLossFunction.getLoss(correctedLabelPredictionWeights);
    }

    @Override
    public String getName() {
        return "DOWNSAMPLED_" + wrappedLossFunction.getName();
    }

    public PredictionMapResults correctLabelPredictionWeights(PredictionMapResults uncorrectedPredictionMapResults) {
        List<PredictionMapResult> results = Lists.newArrayList();
        for (PredictionMapResult result : uncorrectedPredictionMapResults) {
            results.add(correctionFunction.getCorrectedLabelPredictionWeight(result));
        }
        return new PredictionMapResults(results);
    }

    // TODO[mk] - internal class, doesn't need to be an interface
    public interface CorrectionFunction {
        PredictionMapResult getCorrectedLabelPredictionWeight(LabelPredictionWeight<Serializable, PredictionMap> labelPredictionWeight);
    }

    public class NegativeInstanceCorrectionFunction implements CorrectionFunction {
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
        public PredictionMapResult getCorrectedLabelPredictionWeight(LabelPredictionWeight<Serializable, PredictionMap> labelPredictionWeight) {
            PredictionMap correctedPredictionMap = PredictionMap.newMap();
            PredictionMap uncorrectedPrediction = labelPredictionWeight.getPrediction();
            double correctedProbability;
            for (Serializable key : uncorrectedPrediction.keySet()) {
                if (key.equals(negativeLabel)) {
                    correctedProbability = 1.0 - DownsamplingUtils.correctProbability(dropProbability, 1.0 - uncorrectedPrediction.get(key));
                    correctedPredictionMap.put(key, correctedProbability);
                } else {
                    correctedProbability = DownsamplingUtils.correctProbability(dropProbability, uncorrectedPrediction.get(key));
                    correctedPredictionMap.put(key, correctedProbability);
                }
            }
            double correctedWeight = labelPredictionWeight.getWeight();
            if (labelPredictionWeight.getLabel().equals(negativeLabel))
                correctedWeight/=(1.0 - dropProbability);
            return new PredictionMapResult(correctedPredictionMap, labelPredictionWeight.label, correctedWeight);
        }

    }
}
