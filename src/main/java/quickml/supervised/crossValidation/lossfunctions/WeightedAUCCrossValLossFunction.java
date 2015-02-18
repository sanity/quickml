package quickml.supervised.crossValidation.lossfunctions;

import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;

import java.io.Serializable;
import java.util.*;

/**
 * AUCCrossValLoss calculates the ROC area over the curve to determine loss.
 * <p/>
 * Created by Chris on 5/5/2014.
 */
public class WeightedAUCCrossValLossFunction extends ClassifierLossFunction {

    private final Serializable positiveClassification;

    public WeightedAUCCrossValLossFunction(Serializable positiveClassification) {
        this.positiveClassification = positiveClassification;
    }

    @Override
    public Double getLoss(PredictionMapResults results) {
        List<AUCData> aucDataList = getAucDataList(results);

        //order by probability ascending
        Collections.sort(aucDataList);

        ArrayList<AUCPoint> aucPoints = getAUCPointsFromData(aucDataList);

        return getAUCLoss(aucPoints);
    }

    @Override
    public String getName() {
        return "WEIGHTED_AUC";
    }

    private List<AUCData> getAucDataList(PredictionMapResults results) {
        ensureBinaryClassifications(results);
        List<AUCData> aucDataList = new ArrayList<>();
        for (PredictionMapResult result : results) {
            double probabilityOfPositiveClassification = result.getPrediction().get(positiveClassification);
            aucDataList.add(new AUCData(result.getLabel(), result.getWeight(), probabilityOfPositiveClassification));
        }
        return aucDataList;
    }

    private void ensureBinaryClassifications(PredictionMapResults results) {
        Set<Serializable> classifications = new HashSet<>();
        for (PredictionMapResult result : results) {
            classifications.add(result.getLabel());
            if (classifications.size() > 2) {
                throw new RuntimeException("AUCCrossValLoss only supports binary classifications");
            }
        }
    }

    protected ArrayList<AUCPoint> getAUCPointsFromData(List<AUCData> aucDataList) {
        double truePositives = 0;
        double trueNegatives = 0;
        double falsePositives = 0;
        double falseNegatives = 0;

        ArrayList<AUCPoint> aucPoints = new ArrayList<>();
        double threshold = 0.0;
        for (AUCData aucData : aucDataList) {
            if (aucData.getClassification().equals(positiveClassification)) {
                truePositives += aucData.getWeight();
            } else {
                falsePositives += aucData.getWeight();
            }
        }

        //iterate through each data point updating all points that are changed by the threshold
        for (AUCData aucData : aucDataList) {
            if (threshold != aucData.getProbability()) {
                aucPoints.add(getAUCPoint(truePositives, falsePositives, trueNegatives, falseNegatives));
                threshold = aucData.getProbability();
            }
            //we are positive but guessing negative
            if (aucData.getClassification().equals(positiveClassification)) {
                //add a false negative
                falseNegatives += aucData.getWeight();
                //remove true positive from previous threshold
                truePositives -= aucData.getWeight();
            } else {//we are negative and guessing negative
                //add a true negative
                trueNegatives += aucData.getWeight();
                //remove a false positive from previous threshold
                falsePositives -= aucData.getWeight();
            }

        }
        //add last point
        aucPoints.add(getAUCPoint(truePositives, falsePositives, trueNegatives, falseNegatives));
        return aucPoints;
    }

    protected AUCPoint getAUCPoint(double truePositives, double falsePositives, double trueNegatives, double falseNegatives) {
        double truePositiveRate = (truePositives + falseNegatives == 0) ? 0 : (truePositives / (truePositives + falseNegatives));
        double falsePositiveRate = (falsePositives + trueNegatives == 0) ? 0 : (falsePositives / (falsePositives + trueNegatives));
        return new AUCPoint(falsePositiveRate, truePositiveRate);
    }

    protected double getAUCLoss(ArrayList<AUCPoint> aucPoints) {
        Collections.sort(aucPoints);

        double sumXY = 0.0;
        //Area over curve OR AUCLoss = (2 - sum((x1-x0)(y1+y0)))/2
        for (int i = 1; i < aucPoints.size(); i++) {
            AUCPoint aucPoint1 = aucPoints.get(i);
            AUCPoint aucPoint0 = aucPoints.get(i - 1);
            sumXY += ((aucPoint1.getFalsePositiveRate() - aucPoint0.getFalsePositiveRate()) * (aucPoint1.getTruePositiveRate() + aucPoint0.getTruePositiveRate()));
        }
        return (2.0 - sumXY) / 2.0;
    }

    protected static class AUCPoint implements Comparable<AUCPoint> {
        private final double truePositiveRate;
        private final double falsePositiveRate;

        public AUCPoint(double falsePositiveRate, double truePositiveRate) {
            this.truePositiveRate = truePositiveRate;
            this.falsePositiveRate = falsePositiveRate;
        }

        public double getFalsePositiveRate() {
            return falsePositiveRate;
        }

        public double getTruePositiveRate() {
            return truePositiveRate;
        }

        @Override
        public int compareTo(AUCPoint o) {
            //order by false positive rate ascending, true positive rate ascending
            if (falsePositiveRate > o.falsePositiveRate) {
                return 1;
            } else if (falsePositiveRate < o.falsePositiveRate) {
                return -1;
            } else {
                return Double.compare(truePositiveRate, o.truePositiveRate);
            }
        }
    }

    protected static class AUCData implements Comparable<AUCData> {
        private final Serializable classification;
        private final double weight;
        private final double probability;

        public AUCData(Serializable classification, double weight, double probability) {
            this.classification = classification;
            this.weight = weight;
            this.probability = probability;
        }

        public Serializable getClassification() {
            return classification;
        }

        public double getWeight() {
            return weight;
        }

        public double getProbability() {
            return probability;
        }

        @Override
        public int compareTo(AUCData o) {
            return Double.compare(probability, o.probability);
        }
    }
}
