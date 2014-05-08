package quickdt.crossValidation;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

import java.io.Serializable;
import java.util.*;

/**
 * AUCCrossValLoss calculates the ROC area under the curve to determine loss.
 * Call getTotalLoss will store a data point for all losses added
 * Get average loss will return 1.0 - AUC
 *
 * Created by Chris on 5/5/2014.
 */
public class AUCCrossValLoss implements CrossValLoss<AUCCrossValLoss> {
    private final Serializable positiveClassification;
    private Set<Serializable> classifications = new HashSet<Serializable>();
    private List<AUCData> aucDataList = new ArrayList<AUCData>();

    public AUCCrossValLoss(Serializable positiveClassification) {
        this.positiveClassification = positiveClassification;
    }

    @Override
    public void addLoss(AbstractInstance abstractInstance, PredictiveModel predictiveModel) {
        classifications.add(abstractInstance.getClassification());
        if (classifications.size() > 2) {
            throw new RuntimeException("AUCCrossValLoss only supports binary classifications");
        }
        aucDataList.add(new AUCData(abstractInstance.getClassification(), abstractInstance.getWeight(), predictiveModel.getProbability(abstractInstance.getAttributes(), abstractInstance.getClassification())));
    }

    @Override
    public double getTotalLoss() {
        if (aucDataList.isEmpty()) {
            throw new IllegalStateException("Tried to get AUC but nothing has been reported to AUCCrossValLoss");
        }
        sortDataByProbability(aucDataList);

        ArrayList<AUCPoint> aucPoints = getAUCPointsFromData(aucDataList);

        return getAUC(aucPoints);
    }

    protected ArrayList<AUCPoint> getAUCPointsFromData(List<AUCData> aucDataList) {
        double truePositives = 0;
        double trueNegatives = 0;
        double falsePositives = 0;
        double falseNegatives = 0;

        ArrayList<AUCPoint> aucPoints = new ArrayList<AUCPoint>();
        //calculate with threshold of 0
        for(AUCData aucData : aucDataList) {
            if(aucData.getClassification().equals(positiveClassification)) {
                truePositives += aucData.getWeight();
            } else {
                falsePositives += aucData.getWeight();
            }
        }
        aucPoints.add(getAUCPoint(truePositives, falsePositives, trueNegatives, falseNegatives));

        //iterate through each data point and use that as a threshold, only the point being considered changes
        for(AUCData aucData : aucDataList) {
            //we are positive but guessing negative
            if (aucData.getClassification().equals(positiveClassification)) {
                //add a false negative
                falseNegatives+=aucData.getWeight();
                //remove true positive from previous threshold
                truePositives-=aucData.getWeight();
            } else {//we are negative and guessing negative
                //add a true negative
                trueNegatives+=aucData.getWeight();
                //remove a false positive from previous threshold
                falsePositives-=aucData.getWeight();
            }
            aucPoints.add(getAUCPoint(truePositives, falsePositives, trueNegatives, falseNegatives));
        }
        return aucPoints;
    }

    protected void sortDataByProbability(List<AUCData> aucDataList) {
        //order by probability ascending
        Collections.sort(aucDataList, new Comparator<AUCData>() {
            @Override
            public int compare(AUCData o1, AUCData o2) {
                if (o1.getProbability() > o2.getProbability()) {
                    return 1;
                } else if (o2.getProbability() > o1.getProbability()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }

    protected AUCPoint getAUCPoint(double truePositives, double falsePositives, double trueNegatives, double falseNegatives) {
        double truePositiveRate = (truePositives + falseNegatives == 0) ? 0 : (truePositives / (truePositives + falseNegatives));
        double falsePositiveRate = (falsePositives + trueNegatives == 0) ? 0 : (falsePositives / (falsePositives + trueNegatives));
        return new AUCPoint(falsePositiveRate, truePositiveRate);
    }

    protected double getAUC(ArrayList<AUCPoint> aucPoints) {
        //order by false positive rate ascending, true positive rate ascending
        Collections.sort(aucPoints, new Comparator<AUCPoint>() {
            @Override
            public int compare(AUCPoint o1, AUCPoint o2) {
                if (o1.getFalsePositiveRate() > o2.getFalsePositiveRate()) {
                    return 1;
                } else if (o1.getFalsePositiveRate() < o2.getFalsePositiveRate()) {
                    return -1;
                } else {
                    return o1.getTruePositiveRate() >= o2.getTruePositiveRate() ? 1 : -1;
                }
            }
        });

        double sumXY = 0.0;
        //Area over curve OR AUCLoss = (2 - sum((x1-x0)(y1+y0)))/2
        for(int i = 1; i < aucPoints.size(); i++) {
            AUCPoint aucPoint1 = aucPoints.get(i);
            AUCPoint aucPoint0 = aucPoints.get(i-1);
            sumXY += ((aucPoint1.getFalsePositiveRate() - aucPoint0.getFalsePositiveRate())*(aucPoint1.getTruePositiveRate()+aucPoint0.getTruePositiveRate()));
        }
        return (2.0 - sumXY) / 2.0;
    }


    @Override
    public int compareTo(AUCCrossValLoss o) {
        return 1 - Double.compare(this.getTotalLoss(), o.getTotalLoss());
    }

    protected static class AUCPoint {
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
    }

    protected static class AUCData {
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
    }
}
