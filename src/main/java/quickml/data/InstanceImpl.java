package quickml.data;

import java.io.Serializable;

public class InstanceImpl<R> implements Instance<R>, Serializable {

    private static final long serialVersionUID = -932048363529904511L;

    protected static final double DEFAULT_WEIGHT = 1.0;

    public InstanceImpl(final R attributes, final Serializable label) {
        this(attributes, label, DEFAULT_WEIGHT);
    }

    public InstanceImpl(final R attributes, final Serializable label, final double weight) {
        this.attributes = attributes;
        this.label = label;
        this.weight = weight;
    }

    public R getAttributes() {
        return attributes;
    }

    public Serializable getLabel() {
        return label;
    }

    public InstanceImpl reweight(double newWeight){
        return new InstanceImpl(getAttributes(), getLabel(), newWeight);
    }

    public double getWeight() {
        return weight;
    }

    private R attributes;
    private Serializable label;
    private double weight;

    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final InstanceImpl instance = (InstanceImpl) o;

        if (Double.compare(instance.weight, weight) != 0) return false;
        if (!attributes.equals(instance.attributes)) return false;
        if (!label.equals(instance.label)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        long temp;
        result = attributes.hashCode();
        result = 31 * result + label.hashCode();
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("[attributes=");
        builder.append(attributes);
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