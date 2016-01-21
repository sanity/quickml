package quickml.supervised.tree.regressionTree.valueCounters;

import org.javatuples.Pair;

import quickml.data.instances.ClassifierInstance;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;



public class MeanValueCounter extends ValueCounter<MeanValueCounter> implements Serializable {
    private static final long serialVersionUID = -6821237234748044623L;
    private double accumulatedValue = 0;
    private double weightedNumValues = 0;


    private boolean hasSufficientData = true;

    public void setHasSufficientData(boolean hasSufficientData) {
        this.hasSufficientData = hasSufficientData;
    }

    public double getWeightedNumValues() {
        return weightedNumValues;
    }

    public static MeanValueCounter accumulateAll(final Iterable<? extends RegressionInstance> instances){
        final MeanValueCounter result = new MeanValueCounter();
        for (RegressionInstance instance : instances) {
            result.add(instance.getLabel(), instance.getWeight());
        }
        return result;

    }

    private void add(double value, double weight) {
        this.accumulatedValue+=value*weight;
        this.weightedNumValues +=weight;
    }


    public boolean hasSufficientData() {
        return hasSufficientData;
    }
    public MeanValueCounter() {}

    public MeanValueCounter(Serializable attrVal) {
        super(attrVal);
    }

    public MeanValueCounter(Serializable attrVal, double weightedNumValues, double accumulatedValue) {
        this(attrVal);
        this.weightedNumValues = weightedNumValues;
        this.accumulatedValue = accumulatedValue;
    }

    public boolean isEmpty() {
        return weightedNumValues ==0;
    }

    public MeanValueCounter(MeanValueCounter meanValueCounter) {
        super(meanValueCounter.attrVal);
        this.weightedNumValues += meanValueCounter.weightedNumValues;
        this.accumulatedValue += meanValueCounter.accumulatedValue;
    }

    @Override
    public MeanValueCounter add(final MeanValueCounter other) {
        double weightedNumValues = this.weightedNumValues + other.weightedNumValues;

        return new MeanValueCounter(this.attrVal, weightedNumValues, this.accumulatedValue + other.accumulatedValue);
    }

    public MeanValueCounter subtract(final MeanValueCounter other) {
        double weightedNumValues = this.weightedNumValues - other.weightedNumValues;
        if ( weightedNumValues < 0)
            throw new RuntimeException("shouldn't have neg values");
        return new MeanValueCounter(this.attrVal,  weightedNumValues, this.accumulatedValue - other.accumulatedValue);
    }

    @Override
    public double getTotal() {
        return accumulatedValue;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeanValueCounter that = (MeanValueCounter) o;

        if (this.accumulatedValue != that.accumulatedValue || this.weightedNumValues !=that.weightedNumValues) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return new Pair<Double, Double>(weightedNumValues, accumulatedValue).hashCode();
    }

    @Override
    public String toString() {
        return "weightedNumValues: " + weightedNumValues + ", accumulatedValue: " + accumulatedValue;
    }
}
