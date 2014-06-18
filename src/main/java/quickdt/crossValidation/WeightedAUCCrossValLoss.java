package quickdt.crossValidation;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

import java.io.Serializable;
import java.util.*;

/**
 * AUCCrossValLoss calculates the ROC area over the curve to determine loss.
 *
 * Created by Chris on 5/5/2014.
 */
public class WeightedAUCCrossValLoss implements CrossValLoss {
    private final Serializable positiveClassification;

    public WeightedAUCCrossValLoss(Serializable positiveClassification) {
        this.positiveClassification = positiveClassification;
    }

    @Override
    public double getLoss(List<? extends AbstractInstance> instances, PredictiveModel predictiveModel) {
        if (instances.isEmpty()) {
            throw new IllegalStateException("Tried to get loss from empty data set");
        }

        List<AUCData> aucDataList = getAucDataList(instances, predictiveModel);

        sortDataByProbability(aucDataList);

        ArrayList<AUCPoint> aucPoints = getAUCPointsFromData(aucDataList);

        return getAUCLoss(aucPoints);
    }

    private List<AUCData> getAucDataList(List<? extends AbstractInstance> instances, PredictiveModel predictiveModel) {
        List<AUCData> aucDataList = new ArrayList<AUCData>();
        Set<Serializable> classifications = new HashSet<Serializable>();
        for (AbstractInstance instance : instances) {
            classifications.add(instance.getClassification());
            if (classifications.size() > 2) {
                throw new RuntimeException("AUCCrossValLoss only supports binary classifications");
            }
            aucDataList.add(new AUCData(instance.getClassification(), instance.getWeight(), predictiveModel.getProbability(instance.getAttributes(), positiveClassification)));
        }
        return aucDataList;
    }

    protected ArrayList<AUCPoint> getAUCPointsFromData(List<AUCData> aucDataList) {
        double truePositives = 0;
        double trueNegatives = 0;
        double falsePositives = 0;
        double falseNegatives = 0;

        ArrayList<AUCPoint> aucPoints = new ArrayList<AUCPoint>();
        double threshold = 0.0;
        for(AUCData aucData : aucDataList) {
            if(aucData.getClassification().equals(positiveClassification)) {
                truePositives += aucData.getWeight();
            } else {
                falsePositives += aucData.getWeight();
            }
        }

        //iterate through each data point updating all points that are changed by the threshold
        for(AUCData aucData : aucDataList) {
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

    protected double getAUCLoss(ArrayList<AUCPoint> aucPoints) {
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
