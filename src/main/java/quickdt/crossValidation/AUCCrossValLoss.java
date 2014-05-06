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

    private List<AUCPoint> aucPoints = new ArrayList<AUCPoint>();
    private double truePositives = 0;
    private double trueNegatives = 0;
    private double falsePositives = 0;
    private double falseNegatives = 0;

    public AUCCrossValLoss(Serializable positiveClassification) {
        this.positiveClassification = positiveClassification;
    }

    @Override
    public void addLoss(AbstractInstance abstractInstance, PredictiveModel predictiveModel) {
        classifications.add(abstractInstance.getClassification());
        if (classifications.size() > 2) {
            throw new RuntimeException("AUCCrossValLoss only supports binary classifications");
        }
        Serializable predictedClassification = predictiveModel.getClassificationByMaxProb(abstractInstance.getAttributes());
        addLoss(predictedClassification, predictedClassification.equals(abstractInstance.getClassification()), abstractInstance.getWeight());
    }

    private void addLoss(Serializable predictedClassification, boolean correctClassification, double weight) {
        if (predictedClassification.equals(positiveClassification)) {
            if (correctClassification) {
                truePositives += weight;
            } else {
                falsePositives += weight;
            }
        } else {
            if (correctClassification) {
                trueNegatives += weight;
            } else {
                falseNegatives += weight;
            }
        }
    }

    @Override
    public double getTotalLoss() {
        double truePositiveRate = (truePositives + falseNegatives == 0) ? 0 : truePositives / (truePositives + falseNegatives);
        double falsePositiveRate = (falsePositives + trueNegatives == 0) ? 0 : falsePositives / (falsePositives + trueNegatives);
        aucPoints.add(new AUCPoint(truePositiveRate, falsePositiveRate));

        truePositives = 0;
        trueNegatives = 0;
        falsePositives = 0;
        falseNegatives = 0;
        return 0;
    }

    @Override
    public double getAverageLoss() {
        if (aucPoints.isEmpty()) {
            if (truePositives == 0 && trueNegatives == 0 && falsePositives == 0 && falseNegatives == 0) {
                throw new IllegalStateException("Tried to get AUC but nothing has been reported to AUCCrossValLoss");
            } else {
                //we have data, call getTotalLoss to store the data point
                getTotalLoss();
            }
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
        return 1 - Double.compare(this.getAverageLoss(), o.getAverageLoss());
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
}
