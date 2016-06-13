package quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions;

import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;

import java.io.Serializable;
import java.util.*;

/**
 * AUCCrossValLoss calculates the ROC area over the curve to determine loss.
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

        //order by probabilityOfPositiveClassification ascending
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

    public ArrayList<AUCPoint> getAUCPointsFromData(List<AUCData> aucDataList) {
        double truePositives = 0;
        double trueNegatives = 0;
        double falsePositives = 0;
        double falseNegatives = 0;

        ArrayList<AUCPoint> aucPoints = new ArrayList<>();
        double thresholdForPositiveClassification = 0.0;
        //start at upper right of ROC CURVE where everything is a positive
        for (AUCData aucData : aucDataList) {
            if (aucData.getClassification().equals(positiveClassification)) {
                truePositives += aucData.getWeight();
            } else {
                falsePositives += aucData.getWeight();
            }
        }
        //add 1,1 since we won't get it if we always predict 0.0
        aucPoints.add(getAUCPoint(truePositives, falsePositives, trueNegatives, falseNegatives));
        //iterate through each data point updating all points that are changed by the threshold
        int startIndex = 0;
        double probabilityOfNext = aucDataList.get(0).getProbabilityOfPositiveClassification();
        while (probabilityOfNext <=0.0 && startIndex<aucDataList.size()) {
            AUCData aucData = aucDataList.get(startIndex);
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
            startIndex++;
            probabilityOfNext = aucData.getProbabilityOfPositiveClassification();
        }

        //now compute the non endpoint ROC curve points
        for (int i = startIndex; i< aucDataList.size(); i++) {
            //each computed probability of positive classification is used as a threshold (in ascending order
            //which maps to the the upper right of the ROC curve.

            // At each threshold, we know that at most one data point changed to be classified
            // as a negative (and thus know the complete count of TPs FPs, TN, FN at that ROC point

            //note, we make the threshold inclusive, in the sense that points are labeled positives if they are
            //less than the threshold

            AUCData aucData = aucDataList.get(i);
             double probability = aucData.getProbabilityOfPositiveClassification();

            //no need to double count
            if (thresholdForPositiveClassification != probability && probability!=0.0) {
                aucPoints.add(getAUCPoint(truePositives, falsePositives, trueNegatives, falseNegatives));
                thresholdForPositiveClassification = probability;
            }
            //point is a positive but with the new threshold, we predict it is negative
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
        //adds last non (0, 0) point in roc space.
        if (truePositives !=0 && falsePositives !=0) {
            aucPoints.add(getAUCPoint(truePositives, falsePositives, trueNegatives, falseNegatives));
        }
        // (1,1)
        aucPoints.add(getAUCPoint(0, 0, trueNegatives, falseNegatives));
        return aucPoints;
    }

    public AUCPoint getAUCPoint(double truePositives, double falsePositives, double trueNegatives, double falseNegatives) {
        double truePositiveRate = (truePositives + falseNegatives == 0) ? 0 : (truePositives / (truePositives + falseNegatives));
        double falsePositiveRate = (falsePositives + trueNegatives == 0) ? 0 : (falsePositives / (falsePositives + trueNegatives));
        return new AUCPoint(falsePositiveRate, truePositiveRate);
    }

    public double getAUCLoss(ArrayList<AUCPoint> aucPoints) {
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

    public static class AUCPoint implements Comparable<AUCPoint> {
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

    public static class AUCData implements Comparable<AUCData> {
        private final Serializable classification;
        private final double weight;
        private final double probabilityOfPositiveClassification;

        public AUCData(Serializable classification, double weight, double probabilityOfPositiveClassification) {
            this.classification = classification;
            this.weight = weight;
            this.probabilityOfPositiveClassification = probabilityOfPositiveClassification;
        }

        public Serializable getClassification() {
            return classification;
        }

        public double getWeight() {
            return weight;
        }

        public double getProbabilityOfPositiveClassification() {
            return probabilityOfPositiveClassification;
        }

        @Override
        public int compareTo(AUCData o) {
            return Double.compare(probabilityOfPositiveClassification, o.probabilityOfPositiveClassification);
        }
    }
}
