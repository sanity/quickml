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
        orderByProbability();

        ArrayList<AUCPoint> aucPoints = getAUCPoints();

        return getAUC(aucPoints);
    }

    private ArrayList<AUCPoint> getAUCPoints() {
        double truePositives = 0;
        double trueNegatives = 0;
        double falsePositives = 0;
        double falseNegatives = 0;

        //calculate with threshold of 0 as a baseline, don't store as a point
        for(AUCData aucData : aucDataList) {
            if(aucData.getClassification().equals(positiveClassification)) {
                truePositives += aucData.getWeight();
            } else {
                falsePositives += aucData.getWeight();
            }
        }

        ArrayList<AUCPoint> aucPoints = new ArrayList<AUCPoint>();
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

    private void orderByProbability() {
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

    private AUCPoint getAUCPoint(double truePositives, double falsePositives, double trueNegatives, double falseNegatives) {
        double truePositiveRate = (truePositives + falseNegatives == 0) ? 0 : truePositives / (truePositives + falseNegatives);
        double falsePositiveRate = (falsePositives + trueNegatives == 0) ? 0 : falsePositives / (falsePositives + trueNegatives);
        return new AUCPoint(truePositiveRate, falsePositiveRate);
    }

    public double getAUC(ArrayList<AUCPoint> aucPoints) {
        if (aucPoints.isEmpty()) {
            throw new IllegalStateException("Tried to get AUC but nothing has been reported to AUCCrossValLoss");
        }

        //order by false positive rate
        Collections.sort(aucPoints, new Comparator<AUCPoint>() {
            @Override
            public int compare(AUCPoint o1, AUCPoint o2) {
                return o1.getFalsePositiveRate() >= o2.getFalsePositiveRate() ? 1 : -1;
            }
        });

        double area = 0.0;
        AUCPoint previousAucPoint = null;
        for(AUCPoint aucPoint : aucPoints) {
            area += getArea(previousAucPoint, aucPoint);
            previousAucPoint = aucPoint;
        }

        if (previousAucPoint != null && previousAucPoint.getFalsePositiveRate() < 1) {
            area += getArea(previousAucPoint, new AUCPoint(1, 1));
        }

        return 1.0 - area;
    }
    
    private double getArea(AUCPoint point1, AUCPoint point2) {
        double area = 0;
        if (point2.getFalsePositiveRate() > 0) {
            if (point1 == null) {
                //point1 == 0,0, get the area of the triangle from 0,0 to point2
                area += getAreaTriangle(point2.getFalsePositiveRate(), point2.getTruePositiveRate());
            } else {
                //get the area from the triangle between point1 and point2
                double width = point2.getFalsePositiveRate() - point1.getFalsePositiveRate();
                //area of square from previous point across
                area += getAreaSquare(width, Math.min(point1.getTruePositiveRate(), point2.getTruePositiveRate()));
                //area of triangle
                area += getAreaTriangle(width, Math.abs(point2.getTruePositiveRate() - point1.getTruePositiveRate()));
            }
        }
        return area;
    }

    private double getAreaTriangle(double base, double height) {
        return base * height / 2;
    }

    private double getAreaSquare(double width, double height) {
        return width * height;
    }

    @Override
    public int compareTo(AUCCrossValLoss o) {
        return 1 - Double.compare(this.getTotalLoss(), o.getTotalLoss());
    }

    private class AUCPoint {
        private final double truePositiveRate;
        private final double falsePositiveRate;

        public AUCPoint(double truePositiveRate, double falsePositiveRate) {
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

    private class AUCData {
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
