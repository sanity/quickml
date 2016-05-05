package quickml.supervised.tree.regressionTree.valueCounters;

import org.javatuples.Pair;

import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;



public class MeanValueCounter extends ValueCounter<MeanValueCounter> implements Serializable {
    private static final long serialVersionUID = -6821237234748044623L;
    private double accumulatedValue = 0;
    private double accumulatedSquares = 0;
    private double accumulatedWeight = 0;


    private boolean hasSufficientData = true;

    public void setHasSufficientData(boolean hasSufficientData) {
        this.hasSufficientData = hasSufficientData;
    }


    public double getAccumulatedValue() {
        return accumulatedValue;
    }
    public double getAccumulatedSquares() {
        return accumulatedSquares;
    }

    public static MeanValueCounter accumulateAll(final Iterable<? extends RegressionInstance> instances){
        final MeanValueCounter result = new MeanValueCounter();
        for (RegressionInstance instance : instances) {
            result.update(instance.getLabel(), instance.getWeight());
        }
        return result;

    }

    public void update(double value, double weight) {
        this.accumulatedValue+=value*weight;
        this.accumulatedSquares+=value*value*weight;
        this.accumulatedWeight +=weight;
    }


    public boolean hasSufficientData() {
        return hasSufficientData;
    }
    public MeanValueCounter() {}

    public MeanValueCounter(Serializable attrVal) {
        super(attrVal);
    }

    public MeanValueCounter(Serializable attrVal, double accumulatedWeight, double accumulatedValue, double accumulatedSquares) {
        this(attrVal);
        this.accumulatedWeight = accumulatedWeight;
        this.accumulatedSquares = accumulatedSquares;
        this.accumulatedValue = accumulatedValue;
    }

    public boolean isEmpty() {
        return accumulatedWeight ==0;
    }

    public MeanValueCounter(MeanValueCounter meanValueCounter) {
        super(meanValueCounter.attrVal);
        this.accumulatedWeight += meanValueCounter.accumulatedWeight;
        this.accumulatedValue += meanValueCounter.accumulatedValue;
        this.accumulatedSquares += meanValueCounter.accumulatedSquares;
    }

    @Override
    public MeanValueCounter add(final MeanValueCounter other) {
        double weightedNumValues = this.accumulatedWeight + other.accumulatedWeight;
        double accumulatedValue = this.accumulatedValue + other.accumulatedValue;
        double accumulatedSquares = this.accumulatedSquares + other.accumulatedSquares;
        return new MeanValueCounter(this.attrVal, weightedNumValues, accumulatedValue, accumulatedSquares);
    }

    public MeanValueCounter subtract(final MeanValueCounter other) {
        double weightedNumValues = this.accumulatedWeight - other.accumulatedWeight;
        double accumulatedValue = this.accumulatedValue - other.accumulatedValue;
        double accumulatedSquares = this.accumulatedSquares - other.accumulatedSquares;
        return new MeanValueCounter(this.attrVal, weightedNumValues, accumulatedValue, accumulatedSquares);  }

    @Override
    public double getTotal() {
        return accumulatedWeight;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeanValueCounter that = (MeanValueCounter) o;

        if (this.accumulatedValue != that.accumulatedValue || this.accumulatedWeight !=that.accumulatedWeight) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return new Pair<Double, Double>(accumulatedWeight, accumulatedValue).hashCode();
    }

    @Override
    public String toString() {
        return "accumulatedWeight: " + accumulatedWeight + ", accumulatedValue: " + accumulatedValue
               + ", accumulatedSquares: " + accumulatedSquares;
    }
}
