package quickdt.data;

import java.io.Serializable;

public class Instance<R extends Serializable> implements Serializable {

    private static final long serialVersionUID = -932048363529904511L;

    private static final double DEFAULT_WEIGHT = 1.0;

    private Instance() {

    }

    public Instance create(final R regressors, final Serializable label) {
        return create(regressors, label, DEFAULT_WEIGHT);
    }

    public Instance create(final R regressors, Serializable label, final double weight) {
        return new Instance(regressors, label, weight);
    }

    public Instance(final R attributes, final Serializable label) {
        this(attributes, label, DEFAULT_WEIGHT);
    }

    public Instance(final R attributes, final Serializable label, final double weight) {
        this.regressors = attributes;
        this.label = label;
        this.weight = weight;
    }

    public R getRegressors() {
        return regressors;
    }

    public Serializable getLabel() {
        return label;
    }

    public Instance reweight(double newWeight){
        return new Instance(getRegressors(), getLabel(), newWeight);
    }

    public double getWeight() {
        return weight;
    }

    private R regressors;
    private Serializable label;
    private double weight;

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Instance instance = (Instance) o;

        if (Double.compare(instance.weight, weight) != 0) return false;
        if (!regressors.equals(instance.regressors)) return false;
        if (!label.equals(instance.label)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        result = regressors.hashCode();
        result = 31 * result + label.hashCode();
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[regressors=");
        builder.append(regressors);
        builder.append(", label=");
        builder.append(label);
        if (weight != 1.0) {
            builder.append(", weight=");
            builder.append(weight);
        }
        builder.append("]");
        return builder.toString();
    }
}